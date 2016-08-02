package brockbadgers.foodme.YelpAPI;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Parser {
    /** ---------------------  Search TAG --------------------- */
    private static final String KEY_ROOT="Items";
    private static final String KEY_REQUEST_ROOT="Request";
    private static final String KEY_REQUEST_CONTAINER="IsValid";
    private static final String KEY_ITEM="Item";
    private static final String KEY_ID="ASIN";
    private static final String KEY_ITEM_URL="DetailPageURL";
    private static final String KEY_IMAGE_ROOT="MediumImage";
    private static final String KEY_IMAGE_CONTAINER="URL";
    private static final String KEY_ITEM_ATTR_CONTAINER="ItemAttributes";
    private static final String KEY_ITEM_ATTR_TITLE="Title";
    private static final String KEY_ITEM_ATTR_LIST_PRICE="ListPrice";
    private static final String KEY_ITEM_ATTR_PRICE="Amount";

    private static final String VALUE_VALID_RESPONSE="True";

    //Tags
    //Items,Request,IsValid,Item,ASIN,DetailPageURL,MediumImage,URL,ItemAttributes,Title


    public NodeList getResponseNodeList(String searchResponse)
    {
        Log.i("response", "" + searchResponse);
        Document doc;
        NodeList items = null;
        if (searchResponse != null) {
            try {
                doc = this.getDomElement(searchResponse);
                items = doc.getElementsByTagName(KEY_ROOT);
                Element element=(Element)items.item(0);
                if(isResponseValid(element)){
                    items=doc.getElementsByTagName(KEY_ITEM);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    public Restaurant getRestaurant(NodeList list, int position){
        Restaurant object=new Restaurant();
        Element e = (Element) list.item(position);
        try {
            object.setUrl(this.getValue(e, KEY_ITEM_URL));
            object.setId(this.getValue(e, KEY_ID));
        }
        catch(Exception ex){}

        try {
            //get the title
            if(e.getElementsByTagName(KEY_ITEM_ATTR_CONTAINER) != null)
                object.setTitle(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTR_CONTAINER).item(0))
                        , KEY_ITEM_ATTR_TITLE));
        }
        catch(Exception ex){}

        try {
            //get the image url
            if(e.getElementsByTagName(KEY_IMAGE_ROOT) != null)
                object.setImageUrl(this.getValue((Element) (e.getElementsByTagName(KEY_IMAGE_ROOT).item(0))
                        , KEY_IMAGE_CONTAINER));
        }
        catch(Exception ex){}

        try {
            //get the price
            if(e.getElementsByTagName(KEY_ITEM_ATTR_LIST_PRICE) != null) {
                String price = this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTR_LIST_PRICE).item(0))
                        , KEY_ITEM_ATTR_PRICE);
                if(!price.equals(""))
                    object.setPrice(Double.parseDouble(price)/100);
            }
        }
        catch(Exception ex){}
        return object;
    }

    public boolean isResponseValid(Element element){
        NodeList nList=element.getElementsByTagName(KEY_REQUEST_ROOT);
        Element e=(Element)nList.item(0);
        if(getValue(e, KEY_REQUEST_CONTAINER).equals(VALUE_VALID_RESPONSE)){
            return true;
        }
        return false;
    }

    /** In app reused functions */

    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = (Document) db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return doc;
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE
                            || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
}
