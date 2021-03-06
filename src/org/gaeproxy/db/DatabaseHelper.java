package org.gaeproxy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

  // name of the database file for your application -- change to something
  // appropriate for your app
  private static final String DATABASE_NAME = "gaeproxy.db";
  // any time you make changes to your database objects, you may have to
  // increase the database version
  private static final int DATABASE_VERSION = 5;

  // the DAO object we use to access the SimpleData table
  private Dao<DNSResponse, String> mDnsCacheDao = null;
  private Dao<App, String> mProxiedCacheDao = null;

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /** Close the database connections and clear any cached DAOs. */
  @Override
  public void close() {
    super.close();
    mDnsCacheDao = null;
    mProxiedCacheDao = null;
  }

  /**
   * Returns the Database Access Object (DAO) for our SimpleData class. It
   * will create it or just give the cached value.
   */
  public Dao<DNSResponse, String> getDNSCacheDao() throws SQLException {
    if (mDnsCacheDao == null) {
      mDnsCacheDao = getDao(DNSResponse.class);
      mDnsCacheDao.setObjectCache(false);
    }
    return mDnsCacheDao;
  }

  /**
   * Returns the Database Access Object (DAO) for our SimpleData class. It
   * will create it or just give the cached value.
   */
  public Dao<App, String> getAppDao() throws SQLException {
    if (mProxiedCacheDao == null) {
      mProxiedCacheDao = getDao(App.class);
      mProxiedCacheDao.setObjectCache(false);
    }
    return mProxiedCacheDao;
  }

  /**
   * This is called when the database is first created. Usually you should
   * call createTable statements here to create the tables that will store
   * your data.
   */
  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      Log.i(DatabaseHelper.class.getName(), "onCreate");
      TableUtils.createTable(connectionSource, DNSResponse.class);
      TableUtils.createTable(connectionSource, App.class);
    } catch (SQLException e) {
      Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * This is called when your application is upgraded and it has a higher
   * version number. This allows you to adjust the various data to match the
   * new version number.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
      int newVersion) {
    switch (oldVersion) {
      default:
        try {
          Log.i(DatabaseHelper.class.getName(), "onUpgrade");
          TableUtils.dropTable(connectionSource, DNSResponse.class, true);
          TableUtils.dropTable(connectionSource, App.class, true);
          // after we drop the old databases, we create the new ones
          onCreate(db, connectionSource);
        } catch (SQLException e) {
          Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
          throw new RuntimeException(e);
        }
    }
  }
}
