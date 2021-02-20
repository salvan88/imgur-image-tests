package ru.valijev.a.a;

public enum Errors {

    overSizeLimit("File is over the size limit"),
    invalidType("File type invalid (1)"),
    imageIdReq("An image ID is required for a GET request to /image");


    public final String message;

    Errors(String message) {
        this.message = message;
    }
}
