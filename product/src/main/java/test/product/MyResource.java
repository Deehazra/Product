package test.product;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        productdata();
        return "DATA LOADED!";
    }

    public static void productdata() {

        System.out.println("DB Connection Started");
        // Enter the
        String csvFilePath ="product/src/main/resources/product.csv";
        try {
            BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
            CSVParser records = CSVParser.parse(lineReader, CSVFormat.EXCEL.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            ArrayList<product> products = new ArrayList<product>();
            for (CSVRecord record : records) {
                product product = new product();
                product.setCategory(record.get(0));
                product.setCompany(record.get(1));
                product.setProduct(record.get(2));
                product.setColor(record.get(3));
                product.setDescription(record.get(4));
                product.setPrice(Integer.parseInt(record.get(5)));
                product.setDiscount(Integer.parseInt(record.get(6)));
                product.setNo_of_Items(Integer.parseInt(record.get(7)));
                products.add(product);
            }

            PreparedStatement statement = null;
            Connection con = dbconnection();
            String trunc="TRUNCATE `product_list`";
            System.out.println("Truncating the old database");
            statement = con.prepareStatement(trunc);
            statement.execute();
            con.commit();

            String sql = "INSERT INTO product_list(Category, Comapny, Product, Color, Description, Price, Discount, No_of_Item) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            System.out.println("Creating the New database");
            statement = con.prepareStatement(sql);
            for (product record : products) {
                statement.setString(1, record.getCategory());
                statement.setString(2, record.getCompany());
                statement.setString(3, record.getProduct());
                statement.setString(4, record.getColor());
                statement.setString(5, record.getDescription());
                statement.setInt(6, record.getPrice());
                statement.setInt(7, record.getDiscount());
                statement.setInt(8, record.getNo_of_Items());

                statement.addBatch();
            }
            statement.executeBatch();
            con.commit();
            con.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static Connection dbconnection() {

        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/product_data?", "root", "password");
            System.out.println("connection sucessfull");
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}

