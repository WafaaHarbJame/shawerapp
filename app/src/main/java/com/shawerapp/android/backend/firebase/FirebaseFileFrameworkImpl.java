package com.shawerapp.android.backend.firebase;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shawerapp.android.R;
import com.shawerapp.android.backend.base.ConnectivityUtils;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseStorage;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.operators.flowable.FlowableCreate;
import io.reactivex.internal.operators.single.SingleCreate;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by john.ernest on 24/10/2017.
 */

public class FirebaseFileFrameworkImpl implements FileFramework {

    private static final DecimalFormat format = new DecimalFormat("#.##");

    private static final long MiB = 1024 * 1024;

    private static final long KiB = 1024;

    private static final String PROFILE_IMAGE = "profileImages";

    private static final String QUESTIONS = "questions";

    private static final String ATTACHMENTS = "attachments";

    private Context mContext;

    private LoginUtil mLoginUtil;

    private ConnectivityUtils mUtils;

    private FirebaseStorage mFirebaseStorage;

    private StorageReference mStorageRoot;

    private static final float maxHeight = 1280.0f;

    private static final float maxWidth = 1280.0f;

    @Inject
    public FirebaseFileFrameworkImpl(Application application, LoginUtil loginUtil, ConnectivityUtils utils) {
        mContext = application.getApplicationContext();
        mLoginUtil = loginUtil;
        mUtils = utils;

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRoot = mFirebaseStorage.getReferenceFromUrl("gs://shawerapp-c8594.appspot.com");
    }

    @Override
    public Maybe<String> uploadProfileImage(Uri imageUri) {
        return mUtils.withNetworkCheck(
                openAndCompressFile(imageUri)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMapMaybe(imageBytes -> {
                            String profileImage = "profile_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

                            StorageReference imageRef = mStorageRoot
                                    .child(mLoginUtil.getUserID())
                                    .child(PROFILE_IMAGE)
                                    .child(profileImage);
                            return RxFirebaseStorage
                                    .putBytes(imageRef, imageBytes)
                                    .flatMap(taskSnapshot -> RxFirebaseStorage.getDownloadUrl(imageRef))
                                    .map(Uri::toString);
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> uploadAnswerAttachment(Uri attachmentUri) {
        String attachment = "attachment_" + System.currentTimeMillis() + "." + CommonUtils.getFileExtension(attachmentUri.getPath());

        StorageReference attachmentRef = mStorageRoot
                .child(mLoginUtil.getUserID())
                .child(ATTACHMENTS)
                .child(attachment);

        return mUtils.withNetworkCheck(
                openFile(attachmentUri.getPath()).toMaybe()
                        .flatMap(bytes -> RxFirebaseStorage
                                .putBytes(attachmentRef, bytes)
                                .flatMap(taskSnapshot -> RxFirebaseStorage.getDownloadUrl(attachmentRef))
                                .map(Uri::toString)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<Long> downloadFile(String fileUrl) {
        return new FlowableCreate<Long>(
                emitter -> {
                    StorageReference fileReference = mFirebaseStorage.getReferenceFromUrl(fileUrl);

                    File localFile = new File(Environment.getExternalStorageDirectory() +
                            File.separator +
                            fileReference.getName());

                    if (!localFile.exists()) {
                        if (localFile.createNewFile()) {
                            fileReference
                                    .getFile(localFile)
                                    .addOnProgressListener(taskSnapshot -> {
                                        emitter.onNext(taskSnapshot.getBytesTransferred());
                                    })
                                    .addOnSuccessListener(state -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError);
                        } else {
                            emitter.onError(new Throwable(mContext.getString(R.string.error_file_create)));
                        }
                    } else {
                        emitter.onComplete();
                    }
                }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public boolean isDownloading(String fileUrl) {
        StorageReference fileReference = mFirebaseStorage.getReferenceFromUrl(fileUrl);
        List<FileDownloadTask> tasks = fileReference.getActiveDownloadTasks();

        return tasks.size() > 0;
    }

    @Override
    public boolean isDownloaded(String fileUrl) {
        StorageReference fileReference = mFirebaseStorage.getReferenceFromUrl(fileUrl);

        File localFile = new File(Environment.getExternalStorageDirectory() +
                File.separator +
                fileReference.getName());

        if (!isDownloading(fileUrl)) {
            return localFile.exists() && localFile.length() > 0;
        } else {
            return false;
        }
    }

    @Override
    public File getDownloadedFile(String fileUrl) {
        StorageReference fileReference = mFirebaseStorage.getReferenceFromUrl(fileUrl);

        File localFile = new File(Environment.getExternalStorageDirectory() +
                File.separator +
                fileReference.getName());

        if (!isDownloading(fileUrl) && isDownloaded(fileUrl)) {
            return localFile;
        } else {
            return null;
        }
    }

    @Override
    public String getFileSize(File file) {

        if (!file.isFile()) {
            throw new IllegalArgumentException("Expected a file");
        }
        final double length = file.length();

        if (length > MiB) {
            return format.format(length / MiB) + " MiB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KiB";
        }
        return format.format(length) + " B";
    }

    private Single<byte[]> openFile(String filePath) {
        return Single.<byte[]>create(
                emitter -> emitter.onSuccess(readFile(filePath)))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());
    }

    private Single<byte[]> openAndCompressFile(Uri imageUri) {
        return new SingleCreate<>(
                emitter -> {
                    ContentResolver contentResolver = mContext.getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();

                    String type = mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
                    File imageFile = new File(mContext.getCacheDir(), "tempImg." + type);
                    InputStream inputStream = contentResolver.openInputStream(imageUri);
                    writeStreamToFile(inputStream, imageFile);

                    File compressedImage = compressImage(imageFile.getPath());
                    emitter.onSuccess(readFile(compressedImage));
                });
    }

    private void writeStreamToFile(InputStream input, File file) {
        try {
            try (OutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File compressImage(String imagePath) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            Timber.e(CommonUtils.getExceptionString(exception));
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            Timber.e(CommonUtils.getExceptionString(exception));
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        bmp.recycle();

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException exception) {
            Timber.e(CommonUtils.getExceptionString(exception));
        }
        FileOutputStream out = null;
        String filepath = getFilename();
        try {
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException exception) {
            Timber.e(CommonUtils.getExceptionString(exception));
        }

        return new File(filepath);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private String getFilename() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + mContext.getPackageName()
                + "/Files/Compressed");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mImageName = "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/" + mImageName);
        return uriString;
    }

    private static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    private static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
