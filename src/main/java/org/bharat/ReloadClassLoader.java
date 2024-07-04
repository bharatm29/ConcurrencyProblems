package org.bharat;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ReloadClassLoader extends ClassLoader {
    public ReloadClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        if(!"org.bharat.DiningPhilosopher".equals(name)) {
            return super.loadClass(name);
        }

        try {
            String url = "/home/bharat/ProgramFiles/learning_java/ConcurrenyProblems/src/main/java/org/bharat/DiningPhilosopher.class";

            var file = new File(url);

            URL myUrl = file.toURI().toURL();

            URLConnection connection = myUrl.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int data = input.read();

            while(data != -1){
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            return defineClass("org.bharat.DiningPhilosopher", classData, 0, classData.length);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}