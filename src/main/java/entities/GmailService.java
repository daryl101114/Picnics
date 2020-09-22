package entities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import javafx.collections.ObservableList;
import models.SquareEmail;


import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GmailService {
    private static final String APPLICATION_NAME = "Fancy Picnics";
    private static final String USER = "me";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String[] SCOPES_LIST = {GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY};
    private static final List<String> SCOPES = new ArrayList<>(Arrays.asList(SCOPES_LIST));
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Gmail service;

    public GmailService() {
        final NetHttpTransport HTTP_TRANSPORT;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<SquareEmail> getEmails(Date startDate, Date endDate, long max_results){
        Gmail.Users.Messages.List request;
        List<Message> messageList = new LinkedList<>();
        try {
            do {
                request = service.users().messages().list(USER)
                        .setQ("from:squarespace subject:Form before:" + endDate + " after:" + startDate)
                        .setMaxResults(max_results);

                ListMessagesResponse response = request.execute();
                messageList.addAll(response.getMessages());
                request.setPageToken(response.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() > 0 && max_results > 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SquareEmail> squareEmailList = new LinkedList<>();
        for(Message message : messageList){
            SquareEmail tempSquareEmail = new SquareEmail();
            tempSquareEmail.setId(message.getId());
            tempSquareEmail.setEmailDate(new Date(message.getInternalDate()));
            if(!tempSquareEmail.exists())
                squareEmailList.add(tempSquareEmail);
        }
        return squareEmailList;
    }

    public void getFullMessages(ObservableList<SquareEmail> squareEmails){
        try {
            for (SquareEmail squareEmail : squareEmails) {
                if (squareEmail != null) {
                    Message tmp = service.users().messages().get(USER, squareEmail.getId())
                            .setFormat("FULL")
                            .execute();
                    MessagePart msgPart = tmp.getPayload();
                    String body = getContent(msgPart);
                    body = body.replaceAll("[\\n\\r]", " ").split("\\(Sent via Fancy Picnics <https://www.fancypicnicshouston.com> \\)")[0];
                    String[] bodyArray = body.split("Name: | Email: | Phone number: | How did you hear about us: | Picnic Date: | Time of picnic: | Estimated guest count: | Location of your picnic : | Address: | Type of event: | Picnic style: | Add on preferences: ");
                    List<String> bodyList = new ArrayList<>(Arrays.asList(bodyArray));
                    List<String> trimmedList = new ArrayList<>();
                    for (String item : bodyList) {
                        if (!item.isEmpty())
                            trimmedList.add(item.trim());
                    }

                    String name = trimmedList.get(0);
                    String email = trimmedList.get(1);
                    String phoneNumber = trimmedList.get(2);
                    String source = trimmedList.get(3);
                    String date = trimmedList.get(4);
                    String time = trimmedList.get(5);
                    String guestCount = trimmedList.get(6);
                    String[] locationArray = (trimmedList.get(7)).split(",");
                    String address = trimmedList.get(8);
                    String[] eventArray = (trimmedList.get(9)).split(",");
                    String[] styleArray = (trimmedList.get(10)).split(",");
                    String[] addonsArray = (trimmedList.get(11)).split(",");

                    for (int i = 0; i < locationArray.length; i++) {
                        if (locationArray[i].contains("Home delivery"))
                            locationArray[i] = "Home delivery";
                    }

                    for (int i = 0; i < styleArray.length; i++) {
                        if (styleArray[i].contains("Custom color pallette"))
                            styleArray[i] = "Custom color pallette";
                    }

                    for (int i = 0; i < addonsArray.length; i++) {
                        if (addonsArray[i].contains("Cinema experience"))
                            addonsArray[i] = "Cinema experience";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GmailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));


        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private String getContent(MessagePart msgPart) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            getPlainTextFromMessageParts(msgPart, stringBuilder);
            byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
            return new String(bodyBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncoding: " + e.toString());
            return "Error getting body.";
        }
    }

    private void getPlainTextFromMessageParts(MessagePart msgPart, StringBuilder stringBuilder) {
        switch (msgPart.getMimeType()) {
            case "text/plain":
            case "text/html":
                stringBuilder.append(msgPart.getBody().getData());
                break;
            case "multipart/alternative":
            case "multipart/mixed":
                List<MessagePart> newParts = msgPart.getParts();
                for (MessagePart newPart : newParts) {
                    getPlainTextFromMessageParts(newPart, stringBuilder);
                }
                break;
        }
    }
}
