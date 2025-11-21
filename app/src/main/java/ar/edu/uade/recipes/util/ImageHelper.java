package ar.edu.uade.recipes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageHelper {

    /**
     * Convierte una imagen URI a Base64
     * @param context Context de la aplicación
     * @param imageUri URI de la imagen
     * @return String en formato Base64 o null si hay error
     */
    public static String convertImageToBase64(Context context, Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            // Corregir orientación antes de redimensionar
            Bitmap correctedBitmap = fixImageOrientation(context, imageUri, bitmap);
            Bitmap resized = resizeBitmap(correctedBitmap, 800);
            return bitmapToBase64(resized);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Corrige la orientación de una imagen basándose en los datos EXIF
     * @param context Context de la aplicación
     * @param imageUri URI de la imagen
     * @param bitmap Bitmap original
     * @return Bitmap con orientación corregida
     */
    public static Bitmap fixImageOrientation(Context context, Uri imageUri, Bitmap bitmap) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return bitmap;

            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );

            inputStream.close();

            return rotateBitmap(bitmap, orientation);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /**
     * Rota un bitmap según la orientación EXIF
     * @param bitmap Bitmap original
     * @param orientation Orientación EXIF
     * @return Bitmap rotado
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1, -1);
                break;
            default:
                return bitmap;
        }

        try {
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /**
     * Redimensiona un bitmap manteniendo el aspect ratio
     * @param bitmap Bitmap original
     * @param maxDimension Dimensión máxima (ancho o alto)
     * @return Bitmap redimensionado
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int maxDimension) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap;
        }

        float scale = Math.min((float) maxDimension / width, (float) maxDimension / height);
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Convierte un Bitmap a String Base64
     * @param bitmap Bitmap a convertir
     * @return String en formato Base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    /**
     * Convierte un Bitmap a String Base64 con calidad personalizada
     * @param bitmap Bitmap a convertir
     * @param quality Calidad de compresión (0-100)
     * @return String en formato Base64
     */
    public static String bitmapToBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }
}

