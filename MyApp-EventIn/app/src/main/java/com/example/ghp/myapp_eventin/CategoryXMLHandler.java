package com.example.ghp.myapp_eventin;

/**
 * Created by ghp on 28-Jul-16.
 */

    import android.util.Log;

    import java.util.ArrayList;

    import org.xml.sax.Attributes;
    import org.xml.sax.SAXException;
    import org.xml.sax.helpers.DefaultHandler;

    public class CategoryXMLHandler extends DefaultHandler {

        Boolean currentElement = false;
        String currentValue = "";
        Category item = null;
        private ArrayList<Category> itemsList = new ArrayList<Category>();

        public ArrayList<Category> getItemsList() {
            return itemsList;
        }

        // Called when tag starts 
        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {

            currentElement = true;
            currentValue = "";
            if (localName.equals("category")) {

                item = new Category();
            }

        }

        // Called when tag closing 
        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            currentElement = false;

            /** set value */
            if (localName.equalsIgnoreCase("name")){
                Log.w("localNames",currentValue);
//                item.setName(currentValue.replace("&amp;","&"));
                item.setName(currentValue);
            }else if (localName.equalsIgnoreCase("category")){

                itemsList.add(item);

            }





        }

        // Called to get tag characters 
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {

            if (currentElement) {
                currentValue = currentValue +  new String(ch, start, length);
            }

        }



}
