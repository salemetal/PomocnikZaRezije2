package com.sale.pomocnikzarezije;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.sale.pomocnikzarezije.db.DBHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sale on 19.12.2016..
 */

public class Backup{

    static final String BCKP_DB_FILE_NAME = "PomocnikZaRezije.db";
    static final String DB_MIME = "application/x-sqlite3";

    private static Context context;

    public void backupDBToGD(final GoogleApiClient googleApiClient, final Context context) {

        //query for chesk if bckp file exist
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                        SearchableField.TITLE, BCKP_DB_FILE_NAME),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if(!result.getStatus().isSuccess()) {
                            Toast.makeText(context, R.string.backup_error, Toast.LENGTH_LONG).show();
                        } else
                        {
                            for(Metadata m : result.getMetadataBuffer()) {
                                if(m.getTitle().equals(BCKP_DB_FILE_NAME)){
                                    try
                                    {
                                        deleteBckpFile(googleApiClient, m.getDriveId());
                                        createBckpFileGD(googleApiClient);
                                        Toast.makeText(context, R.string.backup_done, Toast.LENGTH_LONG).show();
                                    }
                                    catch (Exception e) {
                                        Log.e(context.getString(R.string.backup_error), e.getMessage());
                                        Toast.makeText(context, R.string.backup_error, Toast.LENGTH_LONG).show();
                                    }
                                    return;
                                }
                            }
                            //if not found, create bckp file
                            try {
                                createBckpFileGD(googleApiClient);
                                Toast.makeText(context, R.string.backup_done, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(context.getString(R.string.backup_error), e.getMessage());
                                Toast.makeText(context, R.string.backup_error, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void restoreFromGD(final GoogleApiClient googleApiClient, final Context context) {

        //query for chesk if bckp file exist
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                        SearchableField.TITLE, BCKP_DB_FILE_NAME),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if(!result.getStatus().isSuccess()) {
                            Toast.makeText(context, R.string.backup_error, Toast.LENGTH_LONG).show();
                        } else
                        {
                            for(Metadata m : result.getMetadataBuffer()) {
                                if(m.getTitle().equals(BCKP_DB_FILE_NAME)){
                                    try
                                    {
                                        //TODO makni
                                        Toast.makeText(context, "bckp file postoji na Google Drive", Toast.LENGTH_LONG).show();

                                        DriveFile bckpDriveFile = Drive.DriveApi.getFile(googleApiClient, m.getDriveId());
                                        bckpDriveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null)
                                                .setResultCallback(contentsOpenedCallback);



                                    }
                                    catch (Exception e) {
                                        Log.e(context.getString(R.string.backup_error), e.getMessage());
                                        Toast.makeText(context, R.string.restore_error, Toast.LENGTH_LONG).show();
                                    }
                                    return;
                                }
                            }
                            //if not found, create bckp file
                            Toast.makeText(context, "Nema bckp file-a na Google Drive-u.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Error:","Restore error!");
                        return;
                    }

                    byte[] buf = new byte[4096];
                    int bytesRead;

                    // DriveContents object contains pointers
                    // to the actual byte stream
                    DriveContents contents = result.getDriveContents();
                    InputStream inputStream = contents.getInputStream();

                    try {
                        //overwrite db file
                        FileOutputStream fileOutputStream = new FileOutputStream("/data/data/com.sale.pomocnikzarezije/databases/" + DBHandler.DATABASE_NAME);
                        while ((bytesRead = inputStream.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, bytesRead);
                            fileOutputStream.flush();
                        }

                        System.out.println("Backup ok!");


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

    private void createBckpFileGD(final GoogleApiClient googleApiClient)
    {
        final DriveFolder rootFolder = Drive.DriveApi.getRootFolder(googleApiClient);
        final File file = new java.io.File("/data/data/com.sale.pomocnikzarezije/databases/" + DBHandler.DATABASE_NAME);

        if (googleApiClient != null && rootFolder != null && file != null) try {
            // create content from file
            Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                    DriveContents driveContents = driveContentsResult != null && driveContentsResult.getStatus().isSuccess() ?
                            driveContentsResult.getDriveContents() : null;

                    // write file to content, chunk by chunk
                    if (driveContents != null) try {
                        OutputStream outputStream = driveContents.getOutputStream();
                        if (outputStream != null) try {
                            InputStream is = new FileInputStream(file);
                            byte[] buf = new byte[4096];
                            int c;
                            while ((c = is.read(buf, 0, buf.length)) > 0) {
                                outputStream.write(buf, 0, c);
                                outputStream.flush();
                            }
                        }
                        finally { outputStream.close();}

                        // content's COOL, create metadata
                        MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(BCKP_DB_FILE_NAME).setMimeType(DB_MIME).build();

                        // now create file on GooDrive
                        rootFolder.createFile(googleApiClient, meta, driveContents).setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                            @Override
                            public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                                if (driveFileResult != null && driveFileResult.getStatus().isSuccess()) {
                                    DriveFile driveFile = driveFileResult != null && driveFileResult.getStatus().isSuccess() ?
                                            driveFileResult.getDriveFile() : null;
                                    if (driveFile != null) {
                                        // BINGO , file uploaded
                                        driveFile.getMetadata(googleApiClient).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                            @Override
                                            public void onResult(DriveResource.MetadataResult metadataResult) {
                                                if (metadataResult != null && metadataResult.getStatus().isSuccess()) {
                                                    DriveId mDriveId = metadataResult.getMetadata().getDriveId();
                                                }
                                            }
                                        });
                                    }
                                } else { Log.e("Error","Error create bckp file!");}
                            }
                        });
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteBckpFile(GoogleApiClient googleApiClient, DriveId driveId)
    {
        try{
            DriveFile bckpDriveFile = Drive.DriveApi.getFile(googleApiClient, driveId);
            bckpDriveFile.delete(googleApiClient);
        } catch (Exception e){
            Log.e("Delete backup error", e.getMessage());
        }
    }
}
