import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import one.digitalinnovation.Entities.Booking.Booking;
import one.digitalinnovation.Entities.Booking.BookingDates;
import one.digitalinnovation.Entities.User.User;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.hamcrest.Matchers.*;
import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingTests {

    
    public static Faker faker;
    private static RequestSpecification request;
    private static Booking booking;
    private static BookingDates bookingDates;
    private static User user;
    
    @BeforeAll
    public static void setUp(){
        //definindo a url 
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        //definindo um faker para criar infos para o user
        faker = new Faker();
        //criando as infos para o fake
        user =  new User(faker.name().username(), 
                            faker.name().firstName(), 
                            faker.name().lastName(), 
                            faker.internet().safeEmailAddress(), 
                            faker.internet().password(), 
                            faker.phoneNumber().toString());

        //setando as datas de checkin e checkout
        bookingDates = new BookingDates("24-12-2022", "24-01-2023");

        //criando o booking
        booking = new  Booking(user.getFirsname(), 
                                    user.getLastname(), 
                                    (float)faker.number().randomDouble(2, 50, 100000), true, 
                                    bookingDates, 
                                    "Breakfast");
    
        //add filtros para cada request
        RestAssured.filters(new RequestLoggingFilter(),new ResponseLoggingFilter(), new ErrorLoggingFilter());
    }

    @BeforeEach
    void setRequest(){
        //aparace mensagens de erro no console e passando a autenticação
        request = given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .auth().basic("admin", "password123");
    }


    //Booking - GetBookingIds
    @Test @Order(1)
    public void getAllBookingsById_returnOK(){
        Response response = request
                                    .when()
                                        .get("/booking")
                                    .then()
                                        .extract()
                                        .response();

        //afirmaçao de que response não está nulo
        Assertions.assertNotNull(response);
        //afirmaçao de que o status retornado é igual a 200
        Assertions.assertEquals(200, response.statusCode());
    }

    //Booking - GetBooking
    @Test @Order(2)
    public void getBooking_returnOK(){
        int id = 1;
        Response response = request
                                    .when()
                                        .get("/booking/" + id)
                                    .then()
                                        .extract()
                                        .response();

        //afirmaçao de que response não está nulo
        Assertions.assertNotNull(response);
        //afirmaçao de que o status retornado é igual a 200
        Assertions.assertEquals(200, response.statusCode());
    }

    //Booking - GetBooking By firstname(poderia usar lastname, checkin or checkout tbm)
    @Test @Order(3)
    public void getAllBookingsByfirstname_BookingExist_returnOK(){
        request
            .when()
                .queryParam("firstname", "John")
                .get("/booking")
            .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .and()
                .body("results", hasSize(greaterThan(0)));
    }

    //Booking - CreateBooking
    //Tem uns dias que essa api não responde em alguns pontos
    @Test @Order(4)
    public void createBooking_withValidDate_returnNotFound(){
        Booking testBooking = booking;
        given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
        .when()
            .body(testBooking)
            .post("/booking")
        .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .and()
            .time(lessThan(2000L));
    }

    //Booking - UpdateBooking
    //Tem uns dias que essa api não responde em alguns pontos
    @Test @Order(5)
    public void updateBooking_withValidDate_returnOK(){
        Booking updateBooking = new Booking(user.getFirsname(), user.getLastname(), (float) 100,true, bookingDates, "room service plus");

        given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
        .when()
            .body(updateBooking)
            .put("/booking/2")
        .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .and()
            .time(lessThan(2000L));
    }

    //Booking - PartialUpdateBooking
    //Tem uns dias que essa api não responde em alguns pontos
    @Test @Order(6)
    public void partialUpdateBooking_withValidDate_returnOK(){
        Booking partialUpdateBooking = new Booking("Tina","Tônia");

        given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
        .when()
            .body(partialUpdateBooking)
            .patch("/booking/2")
        .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .and()
            .time(lessThan(2000L));
    }


    //Booking - DeleteBooking
    //Tem uns dias que essa api não responde em alguns pontos
    @Test @Order(7)
    public void deleteBooking_BookingExist_returnOK(){
        request
            .when()
                .delete("/booking/1")
            .then()
                .assertThat()
                .statusCode(201);
    }

    //Ping - HealthCheck
    @Test  @Order(8)
    public void healthCheck_returnOK(){
        Response response = request
                                    .when()
                                        .get("/ping")
                                    .then()
                                        .extract()
                                        .response();

        //afirmaçao de que response não está nulo
        Assertions.assertNotNull(response);
        //afirmaçao de que o status retornado é igual a 200
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("HTTP/1.1 201 Created", response.getStatusLine());
    }

    //Auth - CreateToken
    @Test  @Order(9)
    public void authCheck_returnOK(){
        Map<String, String> auth = new HashMap<>();

        // Inserir chave-valor
        auth.put("username", "admin");
        auth.put("password", "password123");

        given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
        .when()
            .body(auth)
            .post("/auth")
        .then()
            .assertThat()
            .statusCode(200)
            .time(lessThan(2000L));
    }
}
