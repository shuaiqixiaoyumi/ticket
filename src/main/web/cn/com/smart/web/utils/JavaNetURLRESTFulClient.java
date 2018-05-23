package cn.com.smart.web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;



public class JavaNetURLRESTFulClient {

    private static final String targetURL = "https://www.yunzhijia.com/openaccess/input/dept/getall";

    public static void main(String[] args) {

           try {

                  URL targetUrl = new URL(targetURL);
                  String keyFile = "D://usr/11104200.key";

  				byte[] keyByte = EncryptUtils.getBytesFromFile(keyFile);

  				Key key = EncryptUtils.restorePrivateKey(keyByte);

                  HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
                  httpConnection.setDoOutput(true);
                  httpConnection.setRequestMethod("POST");
                  httpConnection.setRequestProperty("Content-Type", "application/json");
                  String data = EncryptUtils.encryptWithEncodeBase64UTF8(

							"{}", key);

                  String input = "{\"eid\":\"11104200\",\"data\":"+data+"}";

                  OutputStream outputStream = httpConnection.getOutputStream();
                  outputStream.write(input.getBytes());
                  outputStream.flush();

                  if (httpConnection.getResponseCode() != 200) {
                         throw new RuntimeException("Failed : HTTP error code : "
                                + httpConnection.getResponseCode());
                  }

                  BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                                (httpConnection.getInputStream())));

                  String output;
                  System.out.println("Output from Server:\n");
                  while ((output = responseBuffer.readLine()) != null) {
                         System.out.println(output);
                  }

                  httpConnection.disconnect();

             } catch (MalformedURLException e) {

                  e.printStackTrace();

             } catch (IOException e) {

                  e.printStackTrace();

            }catch(Exception e) {
            	e.printStackTrace();
            }

           }     

}
