import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//         CSV - JSON start
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCSV_JSON = parseCSV(columnMapping, fileName);
        listCSV_JSON.forEach(System.out::println);
        String json_CSV_JSON = listToJson(listCSV_JSON);
        writeString(json_CSV_JSON, "dataCSV_JSON.json");
        System.out.println("CSV - JSON parser complete");
//         CSV - JSON end

//         XML - JSON start
        List<Employee> listXML_JSON = parseXml("data.xml");
        String json_XML_JSON = listToJson(listXML_JSON);
        writeString(json_XML_JSON, "dataXML_JSON.json");
        System.out.println("XML - JSON parser complete");
//         XML - JSON end

//         JSON Parser start
        String json = readString("new_data.json");
        List<Employee> list = jsonToList(json);
        System.out.println("JSON parser complete");
//         JSON Parser end
    }

    public static List<Employee> jsonToList(String json){
        List<Employee> list = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonParser jsonParser = new JsonParser();
        Object obj = jsonParser.parse(json);
        JsonArray jsonArray = (JsonArray) obj;
        for (int i = 0; i < jsonArray.size(); i++){
            Employee employee = gson.fromJson(jsonArray.get(i), Employee.class);
            list.add(employee);
        }
        return list;
    }

    static String readString (String fileName){
        String s = null;
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){            
           s = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static List<Employee> parseXml(String fileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fileName);
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeName().equals("#text")) continue; // не понимаю откуда берется #text приходиться игнорировать
                if (Node.ELEMENT_NODE == node.getNodeType()){
                    Element element = (Element) node;
                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    employees.add(new Employee(id, firstName, lastName, country, age)); // почему-то выделяет IDEA, но работает
                }
            }
        } catch (IOException | SAXException | ParserConfigurationException e){
            e.printStackTrace();
        }
        return employees;
    }

    public static void writeString(String json, String fileName){
        try (FileWriter writer = new FileWriter(new File(fileName))){
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String listToJson(List<Employee> list){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type type = new TypeToken<List<Object>>(){}.getType();
        String json = gson.toJson(list, type);
        return json;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String filename) {
        List<Employee> staff = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException  e) {
            e.printStackTrace();
        }
        return staff;
    }
}


