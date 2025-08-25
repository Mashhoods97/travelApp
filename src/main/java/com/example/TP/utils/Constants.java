package com.example.TP.utils;

public final class Constants {

    public static final class UserType {
        public static final int OWNER = -1;
        public static final int RESELLER = 100;
        public static final int HEAD = 110;
        public static final int MANAGER = 120;
        public static final int SALES_MANAGER = 130;
        public static final int CLIENT = 140;
        public static final int CUSTOMER = 150;

        // Private constructor to prevent instantiation
        private UserType() {
            throw new AssertionError("This class cannot be instantiated");
        }
    }
}
