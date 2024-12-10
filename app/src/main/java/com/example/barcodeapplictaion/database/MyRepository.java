package com.example.barcodeapplictaion.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MyRepository {
    private MyDao myDao;
    private LiveData<List<ScannedEntity>> scannedList;
    private LiveData<List<CreatedEntity>> createdList;
    private LiveData<List<MyEntity>> favList;

    MyRepository(Application application){
        MyDatabase mDB = MyDatabase.getDatabase(application);
        myDao = mDB.myDao();
        scannedList = myDao.getAllScannedItems();
        createdList = myDao.getAllCreatedItems();
        favList = myDao.getAllFavItems();
    }

    // SCANNED ITEMS
    LiveData<List<ScannedEntity>> getScannedList() {
        return scannedList;
    }

    void insertScannedItem(ScannedEntity scannedEntity) {
        new InsertAsyncTask(myDao).execute(scannedEntity);
    }

    void deleteScannedItem(String text) {
        new DeleteAsyncTask(myDao).execute(text);
    }

    void updateScannedItem(ScannedEntity scannedEntity) {
        new UpdateAsyncTask(myDao).execute(scannedEntity);
    }

    //Background Operations
    private static class InsertAsyncTask extends AsyncTask<ScannedEntity, Void, Void> {
        private MyDao dao;

        InsertAsyncTask(MyDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final ScannedEntity... scannedEntities) {
            ScannedEntity scannedEntity = scannedEntities[0];
            dao.insert(scannedEntity);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<String, Void, Void> {
        private MyDao dao;

        public DeleteAsyncTask(MyDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(String... texts) {
            String text = texts[0];
            dao.deleteScannedItem(text);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<ScannedEntity, Void, Void> {
        private MyDao dao;

        UpdateAsyncTask(MyDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ScannedEntity... scannedEntities) {
            dao.updateWord(scannedEntities[0]);
            return null;
        }
    }


    //CREATED ITEMS
    LiveData<List<CreatedEntity>> getCreatedItemsList() {
        return createdList;
    }

    void insertCreatedItem(CreatedEntity createdEntity) {
        new InsertAsyncTaskCreated(myDao).execute(createdEntity);
    }

    void deleteCreatedItem(String text) {
        new DeleteAsyncTaskCreated(myDao).execute(text);
    }

    void updateCreatedItem(CreatedEntity createdEntity) {
        new UpdateAsyncTaskCreated(myDao).execute(createdEntity);
    }

    //Background Operations
    private static class InsertAsyncTaskCreated extends AsyncTask<CreatedEntity, Void, Void> {
        private MyDao dao;

        InsertAsyncTaskCreated(MyDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final CreatedEntity... createdEntities) {
            CreatedEntity createdEntity = createdEntities[0];
            dao.insertCreatedItem(createdEntity);
            return null;
        }
    }

    private static class DeleteAsyncTaskCreated extends AsyncTask<String, Void, Void> {
        private MyDao dao;

        public DeleteAsyncTaskCreated(MyDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(String... texts) {
            String text = texts[0];
            dao.deleteCreatedItem(text);
            return null;
        }
    }

    private static class UpdateAsyncTaskCreated extends AsyncTask<CreatedEntity, Void, Void> {
        private MyDao dao;

        UpdateAsyncTaskCreated(MyDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(CreatedEntity... createdEntities) {
            dao.updateCreatedItem(createdEntities[0]);
            return null;
        }
    }


    // FAVORITE ITEMS
    LiveData<List<MyEntity>> getFavoriteList() {
        return favList;
    }

    void insertFavoriteItem(MyEntity myEntity) {
        new InsertFavoriteAsyncTask(myDao).execute(myEntity);
    }

    void checkFav(String content, byte[] bytes) {
        new CheckFavouriteAsyncTask(myDao, content, bytes);
    }

    void deleteFavoriteItem(String text) {
        new DeleteFavoriteAsyncTask(myDao).execute(text);
    }

    void updateFavouriteItem(MyEntity myEntity) {
        new UpdateFavoriteAsyncTask(myDao).execute(myEntity);
    }

    //Background Operations
    private static class InsertFavoriteAsyncTask extends AsyncTask<MyEntity, Void, Void> {
        private MyDao dao;

        InsertFavoriteAsyncTask(MyDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final MyEntity... myEntities) {
            MyEntity myEntity = myEntities[0];
            dao.insertFavItem(myEntity);
            return null;
        }
    }

    private static class DeleteFavoriteAsyncTask extends AsyncTask<String, Void, Void> {
        private MyDao dao;

        public DeleteFavoriteAsyncTask(MyDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(String... texts) {
            String text = texts[0];
            dao.deleteFavItem(text);
            return null;
        }
    }

    private static class UpdateFavoriteAsyncTask extends AsyncTask<MyEntity, Void, Void> {
        private MyDao dao;

        UpdateFavoriteAsyncTask(MyDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(MyEntity... myEntities) {
            dao.updateFavItem(myEntities[0]);
            return null;
        }
    }


    private static class CheckFavouriteAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private MyDao dao;
        private String content;
        private byte[] bmp;

        CheckFavouriteAsyncTask(MyDao dao, String content, byte[] bmp) {
            this.dao = dao;
            this.content = content;
            this.bmp = bmp;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return dao.checkFavByContentAndImage(content).isEmpty();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public interface FavouriteListener{
        void onFavCheck(boolean isFav);
    }
}
