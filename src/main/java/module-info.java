module org.st.shc {
    requires java.net.http;
    requires javafx.controls;
    requires lombok;
    requires org.slf4j;
    requires jsr305;

    exports org.st.shc;
    exports org.st.shc.framework.i18n;
    exports org.st.shc.services;
    exports org.st.shc.components;
}
