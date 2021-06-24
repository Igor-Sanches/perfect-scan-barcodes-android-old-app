package com.igordutrasanches.perfectscan.compoments;

import android.app.Activity;
import android.content.Context;

public class CodigoActionDelegate {
    private Context mContext;
    private Activity mActivity;
    private String codigo;
    public CodigoActionDelegate(Context context){
        this.mContext = context;
    }
    public CodigoActionDelegate(String codigo, Context m){
        this.codigo = codigo;
        this.mContext = m;
    }

    public CodigoActionDelegate by(Context c){ return new CodigoActionDelegate(c); }
    public CodigoActionDelegate by(String codigo, Context c){ return new CodigoActionDelegate(codigo, c); }

    /*
    static String formatoTelefone(String numero){
        return PhoneNumberUtils.formatNumber(numero);
    }

    public void lancarIntent(Intent intent){
        try{
            iniciarLancamento(intent);
        }catch (ActivityNotFoundException x){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.msg_intent_failed);
            builder.setPositiveButton(R.string.button_ok, null);
            builder.show();
        }
    }

    final void addPhoneOnlyContact(String[] phoneNumbers,String[] phoneTypes) {
        addContact(null, null, null, phoneNumbers, phoneTypes, null, null, null, null, null, null, null, null, null, null, null);
    }

    final void addEmailOnlyContact(String[] emails, String[] emailTypes) {
        addContact(null, null, null, null, null, emails, emailTypes, null, null, null, null, null, null, null, null, null);
    }

    final void addContact(String[] names,
                          String[] nicknames,
                          String pronunciation,
                          String[] phoneNumbers,
                          String[] phoneTypes,
                          String[] emails,
                          String[] emailTypes,
                          String note,
                          String instantMessenger,
                          String address,
                          String addressType,
                          String org,
                          String title,
                          String[] urls,
                          String birthday,
                          String[] geo) {

        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        putExtra(intent, ContactsContract.Intents.Insert.NAME, names != null && names.length > 0 ? names[0] : null);

        putExtra(intent, ContactsContract.Intents.Insert.PHONETIC_NAME, pronunciation);

        if (phoneNumbers != null) {
            int phoneCount = Math.min(phoneNumbers.length, Contents.PHONE_KEYS.length);
            for (int x = 0; x < phoneCount; x++) {
                putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
                if (phoneTypes != null && x < phoneTypes.length) {
                    int type = toPhoneContractType(phoneTypes[x]);
                    if (type >= 0) {
                        intent.putExtra(Contents.PHONE_TYPE_KEYS[x], type);
                    }
                }
            }
        }

        if (emails != null) {
            int emailCount = Math.min(emails.length, Contents.EMAIL_KEYS.length);
            for (int x = 0; x < emailCount; x++) {
                putExtra(intent, Contents.EMAIL_KEYS[x], emails[x]);
                if (emailTypes != null && x < emailTypes.length) {
                    int type = toEmailContractType(emailTypes[x]);
                    if (type >= 0) {
                        intent.putExtra(Contents.EMAIL_TYPE_KEYS[x], type);
                    }
                }
            }
        }

        ArrayList<ContentValues> data = new ArrayList<>();
        if (urls != null) {
            for (String url : urls) {
                if (url != null && !url.isEmpty()) {
                    ContentValues row = new ContentValues(2);
                    row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                    row.put(ContactsContract.CommonDataKinds.Website.URL, url);
                    data.add(row);
                    break;
                }
            }
        }

        if (birthday != null) {
            ContentValues row = new ContentValues(3);
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
            row.put(ContactsContract.CommonDataKinds.Event.START_DATE, birthday);
            data.add(row);
        }

        if (nicknames != null) {
            for (String nickname : nicknames) {
                if (nickname != null && !nickname.isEmpty()) {
                    ContentValues row = new ContentValues(3);
                    row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
                    row.put(ContactsContract.CommonDataKinds.Nickname.TYPE,
                            ContactsContract.CommonDataKinds.Nickname.TYPE_DEFAULT);
                    row.put(ContactsContract.CommonDataKinds.Nickname.NAME, nickname);
                    data.add(row);
                    break;
                }
            }
        }

        if (!data.isEmpty()) {
            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
        }

        StringBuilder aggregatedNotes = new StringBuilder();
        if (note != null) {
            aggregatedNotes.append('\n').append(note);
        }
        if (geo != null && geo.length >= 2) {
            aggregatedNotes.append('\n').append(geo[0]).append(',').append(geo[1]);
        }

        if (aggregatedNotes.length() > 0) {
            // Remove extra leading '\n'
            putExtra(intent, ContactsContract.Intents.Insert.NOTES, aggregatedNotes.substring(1));
        }

        if (instantMessenger != null && instantMessenger.startsWith("xmpp:")) {
            intent.putExtra(ContactsContract.Intents.Insert.IM_PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER);
            intent.putExtra(ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger.substring(5));
        } else {
            putExtra(intent, ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger);
        }

        putExtra(intent, ContactsContract.Intents.Insert.POSTAL, address);
        if (addressType != null) {
            int type = toAddressContractType(addressType);
            if (type >= 0) {
                intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, type);
            }
        }
        putExtra(intent, ContactsContract.Intents.Insert.COMPANY, org);
        putExtra(intent, ContactsContract.Intents.Insert.JOB_TITLE, title);
        launchIntent(intent);
    }

    private static int toEmailContractType(String typeString) {
        return doToContractType(typeString, EMAIL_TYPE_STRINGS, EMAIL_TYPE_VALUES);
    }

    private static int toPhoneContractType(String typeString) {
        return doToContractType(typeString, PHONE_TYPE_STRINGS, PHONE_TYPE_VALUES);
    }

    private static int toAddressContractType(String typeString) {
        return doToContractType(typeString, ADDRESS_TYPE_STRINGS, ADDRESS_TYPE_VALUES);
    }

    private static int doToContractType(String typeString, String[] types, int[] values) {
        if (typeString == null) {
            return NO_TYPE;
        }
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            if (typeString.startsWith(type) || typeString.startsWith(type.toUpperCase(Locale.ENGLISH))) {
                return values[i];
            }
        }
        return NO_TYPE;
    }

    final void shareByEmail(String contents) {
        sendEmail(null, null, null, null, contents);
    }

    final void sendEmail(String[] to,
                         String[] cc,
                         String[] bcc,
                         String subject,
                         String body) {
        Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        if (to != null && to.length != 0) {
            intent.putExtra(Intent.EXTRA_EMAIL, to);
        }
        if (cc != null && cc.length != 0) {
            intent.putExtra(Intent.EXTRA_CC, cc);
        }
        if (bcc != null && bcc.length != 0) {
            intent.putExtra(Intent.EXTRA_BCC, bcc);
        }
        putExtra(intent, Intent.EXTRA_SUBJECT, subject);
        putExtra(intent, Intent.EXTRA_TEXT, body);
        intent.setType("text/plain");
        launchIntent(intent);
    }

    final void shareBySMS(String contents) {
        sendSMSFromUri("smsto:", contents);
    }

    final void sendSMS(String phoneNumber, String body) {
        sendSMSFromUri("smsto:" + phoneNumber, body);
    }

    private void sendSMSFromUri(String uri, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        putExtra(intent, "sms_body", body);
        // Exit the app once the SMS is sent
        intent.putExtra("compose_mode", true);
        launchIntent(intent);
    }

    final void sendMMS(String phoneNumber, String subject, String body) {
        sendMMSFromUri("mmsto:" + phoneNumber, subject, body);
    }

    private void sendMMSFromUri(String uri, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        // The Messaging app needs to see a valid subject or else it will treat this an an SMS.
        if (subject == null || subject.isEmpty()) {
            putExtra(intent, "subject", activity.getString(R.string.msg_default_mms_subject));
        } else {
            putExtra(intent, "subject", subject);
        }
        putExtra(intent, "sms_body", body);
        intent.putExtra("compose_mode", true);
        launchIntent(intent);
    }

    final void dialPhone(String phoneNumber) {
        lancarIntent(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    final void dialPhoneFromUri(String uri) {
        lancarIntent(new Intent(Intent.ACTION_DIAL, Uri.parse(uri)));
    }

    final void openProductSearch(String upc) {
        Uri uri = Uri.parse("http://www.google." + LocaleManager.getProductSearchCountryTLD(activity) +
                "/m/products?q=" + upc + "&source=zxing");
        lancarIntent(new Intent(Intent.ACTION_VIEW, uri));
    }
    public void urlOpen(String url) {
        if (url.startsWith("HTTP://")) {
            url = "http" + url.substring(4);
        } else if (url.startsWith("HTTPS://")) {
            url = "https" + url.substring(5);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            lancarIntent(intent);
        } catch (ActivityNotFoundException ignored) {
            Log.w(TAG, "Nothing available to handle " + intent);
        }
    }


    public void buscarNaWeb(String objeto){
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra("query", objeto);
        lancarIntent(intent);
    }

    private void iniciarLancamento(Intent intent) {
        if(intent != null){
            intent.addFlags(Intents.FLAG_NEW_DOC);
            mActivity.startActivity(intent);
        }
    }

    public void onSendSMS(){
   // Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + ));
    }*/
}
