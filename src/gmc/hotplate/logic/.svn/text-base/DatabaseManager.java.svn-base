/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.R;
import gmc.hotplate.entities.Product;
import gmc.hotplate.entities.Recipe;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class DatabaseManager implements IDatabaseManager {

    private static final String LOG_TAG = "DatabaseManager";

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private static DatabaseManager sInstance = null;

    private DatabaseManager(Context context) {
        int dbVersion = Integer.parseInt(context.getResources().getString(R.string.db_version));
        String dbName = context.getResources().getString(R.string.db_name);
        dbHelper = new DBHelper(context, dbName, null, dbVersion);
        db = dbHelper.getWritableDatabase();
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context);
        }
        return sInstance;
    }

    @Override
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public Recipe[] getRecipes(int limit) {
        String[] columns = {"_id", "name", "category_id", "description", "person_count", "steps"};
        Cursor cursor = db.query("recipes", columns, null, null, null, null, null,
                 String.valueOf(limit));
        int cursorCount = cursor.getCount();
        Log.d(LOG_TAG, "Cursor size: " + cursorCount);
        Recipe[] recipes = new Recipe[cursorCount];
        Recipe recipe;
        if (cursor.moveToFirst()) {
            do {
                recipe = new Recipe();
                recipe.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                recipe.setName(cursor.getString(cursor.getColumnIndex("name")));
                recipe.setCategoryId(cursor.getInt(cursor.getColumnIndex("category_id")));
                recipe.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                recipe.setPersonCount(cursor.getInt(cursor.getColumnIndex("person_count")));
                //TODO(arhangeldim): To get info about steps.

                recipes[cursor.getPosition()] = recipe;
            } while (cursor.moveToNext());
        }
        return recipes;
    }



    @Override
    public List<Product> getProductsList(int type, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Recipe getRecipeById(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Product getProductById(int id) {
        // TODO Auto-generated method stub
        return null;
    }


    private class DBHelper extends SQLiteOpenHelper {

        private static final String LOG_TAG = "DBHelper";

        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createSimpleDatabase(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        // FIXME(see issues): Method don't satisfy code style rules.
        // TODO(see issues): Rewrite this method.
        private void createSimpleDatabase(SQLiteDatabase db) {
            Log.d(LOG_TAG, "Generating database");
            Log.d(LOG_TAG, "  Creating tables");

            // Creating tables
            db.execSQL("create table categories ("
                      + "_id integer primary key autoincrement,"
                      + "name text" + ");");

            db.execSQL("create table products ("
                      + "_id integer primary key autoincrement,"
                      + "name text,"
                      + "type integer" + ");");

            db.execSQL("create table recipes ("
                      + "_id integer primary key autoincrement,"
                      + "name text,"
                      + "category_id integer,"
                      + "description text,"
                      + "person_count integer,"
                      + "steps text" + ");");

            db.execSQL("create table ingredients ("
                      + "_id integer primary key autoincrement,"
                      + "recipe_id integer,"
                      + "product_id integer,"
                      + "amount float" + ");");

            // Creating Demo Recipes
            Log.d(LOG_TAG, "  Creating Demo Recipes");
            String[] categories = {"Бутерброды", "Супы"};

            String[] productNames = {"Вода", "Топор обыкновенный", "Грибы белые",
                        "Хлеб белый", "Картофель", "Морковь", "Масло сливочное", "Соль"};

            // TODO(djsatok): Check if it really need?
            int[] productTypes = {2, 1, 3, 3, 1, 1, 3, 3};

            String[] recipeNames = {"Бутерброд с маслом", "Суп из топора", "Картофель с грибами"};
            int[] recipeCategories = {1, 2, 1};
            String[] recipeDescriptions = {"Это легкий рецепт для тех, кому лень делать что-либо ещё",
                    "Русский народный рецепт, который помогает изобличить жадин",
                    "Коронный рецепт разработчика Димы"};
            int[] recipePeople = {1, 4, 2};
            String[] recipeSteps = new String[3];

            recipeSteps[0] = "<steps>" +
                             "  <step>" +
                             "      <id> 1 </id>" +
                             "      <name> Начало </name>" +
                             "      <description> Отрезаем кусочек хлеба </description>" +
                             "      <time> 0 </time>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 2 </id>" +
                             "      <name> Середина </name>" +
                             "      <description> Намазываем масло на хлеб </description>" +
                             "      <time> 0 </time>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 3 </id>" +
                             "      <name> Конец </name>" +
                             "      <description>" +
                             "Для выносливых: ждём 1 минуту, наслаждаясь" +
                             " сладостным ароматом бутерброда, а потом съедаем его! </description>" +
                             "      <time> 60 </time>" +
                             "  </step>" +
                             "</steps>";

            recipeSteps[1] = "<steps>" +
                             "  <step>" +
                             "      <id> 1 </id>" +
                             "      <name> Кипение </name>" +
                             "      <description> Заливаем воду в кастрюлю и доводим ее до кипения" +
                             " </description>" +
                             "      <time> 0 </time>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 2 </id>" +
                             "      <name> Топор </name>" +
                             "      <description> Кидаем в кипящую воду топор и варим его 60 сек. </description>" +
                             "      <time> 60 </time>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 3 </id>" +
                             "      <name> Режем картошку </name>" +
                             "      <description> Режем картошку. </description>" +
                             "      <time> 0 </time>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 4 </id>" +
                             "      <name> Кидаем картошку в суп </name>" +
                             "      <description> Кидаем в кипящую воду с топором картошку и варим их 60 сек. </description>" +
                             "      <time> 60 </time>" +
                             "      <blocklist>" +
                             "          <item> 2 </item>" +
                             "          <item> 3 </item>" +
                             "      </blocklist>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 5 </id>" +
                             "      <name> Режем морковку </name>" +
                             "      <description> Режем морковь. </description>" +
                             "      <time> 0 </time>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 6 </id>" +
                             "      <name> Кидаем морковку в суп </name>" +
                             "      <description> Кидаем в кипящую воду с топором и картошкой морковь " +
                             "и варим их 60 сек. </description>" +
                             "      <time> 60 </time>" +
                             "      <blocklist>" +
                             "          <item> 4 </item>" +
                             "          <item> 5 </item>" +
                             "      </blocklist>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 7 </id>" +
                             "      <name> Солим </name>" +
                             "      <description> Солим суп, после чего его можно кушать! </description>" +
                             "      <time> 0 </time>" +
                             "  </step>" +
                             "</steps>";

            recipeSteps[2] = "<steps>" +
                             "  <step>" +
                             "      <id> 1 </id>" +
                             "      <name> Картошка </name>" +
                             "      <description> Режем картошку и ставим её жариться на 60 сек. </description>" +
                             "      <time> 60 </time>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 2 </id>" +
                             "      <name> Грибы </name>" +
                             "      <description> Режем грибы и ставим их жариться на 40 сек. </description>" +
                             "      <time> 40 </time>" +
                             "  </step>" +
                             "  <step>" +
                             "      <id> 3 </id>" +
                             "      <name> Смешивание </name>" +
                             "      <description> Смешиваем картошку и грибы и кушаем всё это! </description>" +
                             "      <time> 0 </time>" +
                             "      <blocklist>" +
                             "          <item> 1 </item>" +
                             "          <item> 2 </item>" +
                             "      </blocklist>" +
                             "  </step>" +
                             "</steps>";


            int[] ingredientsRecipe = {1, 1, 2, 2, 2, 2, 2, 3, 3};
            int[] ingredientsProduct = {4, 7, 1, 2, 5, 6, 8, 3, 5};
            float[] ingredientAmount = {1, 10, 2, 1, 5, 5, 10, 500, 5};

            ContentValues cv = new ContentValues();

            for (int i = 0; i < categories.length; i++ ){
                cv.clear();
                cv.put("name", categories[i]);
                db.insert("categories", null, cv);
            }

            for (int i = 0; i < productNames.length; i++){
                cv.clear();
                cv.put("name", productNames[i]);
                cv.put("type", productTypes[i]);
                db.insert("products", null, cv);
            }

            for (int i = 0; i < recipeCategories.length; i++){
                cv.clear();
                cv.put("name", recipeNames[i]);
                cv.put("description", recipeDescriptions[i]);
                cv.put("steps", recipeSteps[i]);
                cv.put("category_id", recipeCategories[i]);
                cv.put("person_count", recipePeople[i]);
                db.insert("recipes", null, cv);
            }

            for (int i = 0 ; i < ingredientAmount.length; i++){
                cv.clear();
                cv.put("recipe_id", ingredientsRecipe[i]);
                cv.put("product_id", ingredientsProduct[i]);
                cv.put("amount", ingredientAmount[i]);
                db.insert("ingredients", null, cv);
            }
            cv.clear();

            Log.d(LOG_TAG, "  Tables created");
        }

    }


    @Override
    public Recipe[] getRecipes(Product ingridient, int categoryId, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Recipe[] getRecipes(Product ingridient, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Recipe[] getRecipes(int categoryId, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

}
