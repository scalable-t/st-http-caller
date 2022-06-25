module org.openjfx {
    requires java.net.http;
    requires javafx.controls;
    requires lombok;
    requires org.slf4j;
    requires jsr305;

    exports org.st.shc;
    exports org.st.shc.components;
}
