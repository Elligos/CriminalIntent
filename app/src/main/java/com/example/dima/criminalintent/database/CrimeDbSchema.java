package com.example.dima.criminalintent.database;

/**
 * Created by Dima on 27.09.2016.
 */

//определение строковых констант для таблицы в БД
public class CrimeDbSchema {
        public final static class CrimeTable{
            public final static String Name = "crimes";

            public static final class Cols{
                public static final String UUID = "uuid";
                public static final String TITLE = "title";
                public static final String DATE = "date";
                public static final String SOLVED = "solved";
                public static final String SUSPECT = "suspect";
            }
        }
}
