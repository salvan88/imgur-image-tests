package ru.valijev.a.a;

public enum Images {

    smallJpg("/src/test/resources/bob.jpg"),
    smallBmpExample("src/test/resources/1.bmp"),
    audioFile("src/test/resources/testAudioExample.mp3"),
    pdfFile("src/test/resources/pdfExample.pdf"),
    videoFile("src/test/resources/videoExample(8mb).mp4"),
    txtFile("src/test/resources/txtFileInBmpFormat.bmp"),
    bigImageFile("src/test/resources/11mb.bmp"),
    gifExample("src/test/resources/gifExample.gif");

    public final String path;

    Images(String path) {
        this.path = path;
    }
}
