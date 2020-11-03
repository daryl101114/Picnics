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
import models.SquareEmail;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public List<SquareEmail> getEmails(LocalDate startDate, LocalDate endDate, long max_results){
        int counter = 1;
        long gmailResults = 499;
        Gmail.Users.Messages.List request;
        List<Message> messageList = new LinkedList<>();
        String epochStartDateInSeconds = Long.toString(java.sql.Date.valueOf(startDate).getTime() / 1000);
        String epochEndDateInSeconds = Long.toString(java.sql.Date.valueOf(endDate).getTime() / 1000);
        try {
            do {
                request = service.users().messages().list(USER)
                        .setQ("from:squarespace subject:Form after:" + epochStartDateInSeconds + " before:" + epochEndDateInSeconds)
                        .setMaxResults(gmailResults);

                ListMessagesResponse response = request.execute();
                try {
                    messageList.addAll(response.getMessages());
                } catch (NullPointerException e){
                    return new ArrayList<>();
                }
                request.setPageToken(response.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() > 0 && gmailResults > 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String startDateString = startDate.format(formatter);
        String endDateString = endDate.format(formatter);

        List<String> squareEmailIdList = SquareEmail.getAllIdWithinDate(startDateString, endDateString);
        List<SquareEmail> squareEmailList = new LinkedList<>();
        for(Message message : messageList){
            if(!squareEmailIdList.contains(message.getId())) {
                counter++;
                SquareEmail tempSquareEmail = new SquareEmail();
                tempSquareEmail.setId(message.getId());
                squareEmailList.add(tempSquareEmail);
                if(counter > max_results)
                    break;
            }
        }
        return getFullMessages(squareEmailList);
    }

    public List<SquareEmail> getFullMessages(List<SquareEmail> squareEmails){
        for (SquareEmail squareEmail : squareEmails) {
            try {
                if (squareEmail != null) {
                    Message tmp = service.users().messages().get(USER, squareEmail.getId())
                            .setFormat("FULL")
                            .execute();
                    MessagePart msgPart = tmp.getPayload();
                    String body = getContent(msgPart);
                    body = body.replaceAll("[\\n\\r]", " ");

                    if(body.contains("html lang")) {
                        body = body.substring(0, body.indexOf("html lang"));
                    }
                    SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
                    Date latestChangeDate = dateformat.parse("Oct 27 2020 20:00:00");
                    Date previousChangeDate = dateformat.parse("Oct 12 2020 00:00:00");
                    Date previousChangeDate2 = dateformat.parse("Oct 08 2020 08:00:00");
                    String[] bodyArray;
                    int maxRows;
                    int counter = 1;
                    if(new Date(tmp.getInternalDate()).after(latestChangeDate)) {
                        bodyArray = body.split("Name: | Email: | Phone number: | How did you hear about us: | Picnic Date: | Time of picnic: | Guest Count: | Location of your picnic: | Provide your address ONLY if your picnic will be a home delivery: | Type of Event: | Picnic Style: | Is there a specific color palette or theme that you are requesting\\?: | Add on preferences: | If you chose marquee letters, please let us know which word or number you   would like:");
                        maxRows = 15;
                    }
                    else if(new Date(tmp.getInternalDate()).after(previousChangeDate)){
                        bodyArray = body.split("Name: | Email: | Phone number: | How did you hear about us: | Picnic Date: | Time of picnic: | Estimated guest count: | Location of your picnic : | Provide your address ONLY if your picnic will be a home delivery: | Type of event: | Picnic style: | Is there a specific color palette or theme that you are requesting\\?: | Add on preferences: | If you chose marquee letters, please let us know which word or number you   would like:");
                        maxRows = 15;
                    } else if(new Date(tmp.getInternalDate()).after(previousChangeDate2)){
                        bodyArray = body.split("Name: | Email: | Phone number: | How did you hear about us: | Picnic Date: | Time of picnic: | Estimated guest count: | Location of your picnic : | Provide your address if your picnic will be a home delivery: | Type of event: | Picnic style: | Which color palette or theme are you requesting\\?: | Add on preferences: | If you chose marquee letters, please let us know which word or number you   would like:");
                        maxRows = 14;
                    }
                    else {
                        bodyArray = body.split("Name: | Email: | Phone number: | How did you hear about us: | Picnic Date: | Time of picnic: | Estimated guest count: | Location of your picnic : |  Address: | Type of event: | Picnic style: | Add on preferences: | If you chose marquee letters, please let us know which word or number you\\'d  like:");
                        maxRows = 13;
                    }
                    List<String> bodyList = new ArrayList<>(Arrays.asList(bodyArray));
                    List<String> trimmedList = new ArrayList<>();
                    for (String item : bodyList) {
                        if(counter > maxRows)
                            break;
                        if (item.contains("Sent via form submission from Fancy Picnics")){
                            item = item.split("Sent via form submission from Fancy Picnics")[0];
                        }
                        trimmedList.add(item.trim());
                        counter++;
                    }

                    squareEmail.setEmailDate(new Date(tmp.getInternalDate()));
                    squareEmail.setEventName(trimmedList.get(1));
                    squareEmail.setEventEmail(trimmedList.get(2));
                    squareEmail.setEventPhoneNumber(trimmedList.get(3));
                    squareEmail.setEventSource(trimmedList.get(4));
                    squareEmail.setEventDate(trimmedList.get(5));
                    squareEmail.setEventTime(trimmedList.get(6));
                    squareEmail.setEventGuestCount(trimmedList.get(7));
                    squareEmail.setEventLocation((trimmedList.get(8).split("\\(")[0].trim()));
                    squareEmail.setEventAddress(trimmedList.get(9));
                    squareEmail.setEventType(trimmedList.get(10));
                    squareEmail.setEventStyle(trimmedList.get(11).split("\\(")[0].trim());
                    squareEmail.setCustomPalette(trimmedList.get(12));
                    squareEmail.setEventAddonsArray((trimmedList.get(13)).split(","));
                    String[] eventAddonsArray = squareEmail.getEventAddonsArray();
                    for (int i = 0; i < eventAddonsArray.length; i++) {
                            eventAddonsArray[i] = eventAddonsArray[i].split("\\(")[0].trim();
                    }
                    squareEmail.setEventAddonsArray(eventAddonsArray);
                    if(maxRows > 14)
                        squareEmail.setMarqueeLetters(trimmedList.get(14).split("Sent via form submission from Fancy Picnics")[0]);
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return squareEmails;
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
        getPlainTextFromMessageParts(msgPart, stringBuilder);
        byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
        return new String(bodyBytes, StandardCharsets.UTF_8);
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
