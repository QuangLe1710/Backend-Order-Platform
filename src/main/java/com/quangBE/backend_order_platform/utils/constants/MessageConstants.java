package com.quangBE.backend_order_platform.utils.constants;

public class MessageConstants {

    public record ErrorMessage( int code, String message ) {}

    public record SuccessMessage( int code, String message) {}

    public static class USER {
        // error message USER
        public static final ErrorMessage USERNAME_NOT_FOUND = new ErrorMessage(
                404,
                "Username is not exist in system"
        );

        public static final ErrorMessage USERNAME_OR_PASSWORD_NOT_TRUE = new ErrorMessage(
                404,
                "Username or Password is not true"
        );

        // validation message USER


        // success message USER


    }

    public static class AUTH {
        // error message AUTH
        public static final ErrorMessage INTROSPECT_FAIL = new ErrorMessage(
                400,
                "Introspect fail"
        );

        // validation message AUTH

        // success message AUTH
        public static final SuccessMessage LOGIN_SUCCESSFUL = new SuccessMessage(
                200,
                "Login successfully"
        );

        public static final SuccessMessage INTROSPECT_SUCCESSFUL = new SuccessMessage(
                200,
                "Introspect successfully"
        );
    }

}
