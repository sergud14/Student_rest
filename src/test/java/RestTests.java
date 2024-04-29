import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class RestTests {

    @Test
    @SneakyThrows
    public void Test1_StudentIsPresent() throws JsonProcessingException {
        //добавление студента
        int id = 1;
        Student st1 = new Student();
        st1.setId(1);
        st1.setName("Ivan");
        List marks = new ArrayList<>();
        marks.add(5);
        marks.add(4);
        st1.setMarks(marks);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post();
        //проверка наличия добавленного студента
        Student st2 = RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when().get().then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo(id))
                .extract().as(Student.class);
        Assertions.assertEquals(st1.toString(), st2.toString());
        //удаление добавленного студента(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .when().delete().then()
                .statusCode(200);
    }

    @Test
    @SneakyThrows
    public void Test2_StudentNotPresent() {
        int id = -1;
        RestAssured.given()
                .baseUri("http://localhost:8080/student/-1")
                .contentType(ContentType.JSON)
                .when().get().then()
                .statusCode(404);
    }

    @Test
    @SneakyThrows
    public void Test3_AddStudent() throws JsonProcessingException {
        int id = 1;
        Student st1 = new Student();
        st1.setId(1);
        st1.setName("Ivan");
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post().then()
                .statusCode(201);
        //удаление добавленного студента(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .when().delete().then()
                .statusCode(200);
    }

    @Test
    @SneakyThrows
    public void Test4_UpdateStudent() throws JsonProcessingException {
        //добавление студента
        int id = 1;
        Student st1 = new Student();
        st1.setId(1);
        st1.setName("Ivan");
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post().then()
                .statusCode(201);
        //обновление студента
        Student st2 = new Student();
        st2.setId(1);
        st2.setName("Pete");
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st2))
                .when().post().then()
                .statusCode(201);
        //проверка обновления студента
        Student st3 = RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when().get().then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo(id))
                .extract().as(Student.class);
        Assertions.assertEquals(st3.toString(), st2.toString());
        //удаление добавленного студента(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .when().delete().then()
                .statusCode(200);
    }

    @Test
    @SneakyThrows
    public void Test5_AddStudentWithNullID() throws JsonProcessingException {
        //добавление студента без id
        Student st1 = new Student();
        String name = "Ivan";
        st1.setName(name);
        st1.setId(null);
        ObjectMapper mapper = new ObjectMapper();
        Response response=RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post();
System.out.println(response.body().asString());
        Assertions.assertEquals(201,response.statusCode());
        //проверка добавления студента
        Student st = RestAssured.given()
                .baseUri("http://localhost:8080/student/"+response.body().asString())
                .params("name", name)
                .contentType(ContentType.JSON)
                .when().get().then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue())
                .extract().as(Student.class);

        Assertions.assertEquals(st1.getName(),st.getName());
        Assertions.assertEquals(st1.getMarks(),st.getMarks());

        //удаление добавленного студента(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + response.body().asString())
                .when().delete().then()
                .statusCode(200);
    }

    @Test
    @SneakyThrows
    public void Test6_AddStudentWithNoName() throws JsonProcessingException {
        //добавление студента без имени
        Student st1 = new Student();
        st1.setId(1);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post().then()
                .statusCode(400);
    }

    @Test
    @SneakyThrows
    public void Test7_DeleteStudent() throws JsonProcessingException {
        //добавление студента
        int id = 1;
        Student st1 = new Student();
        st1.setId(1);
        st1.setName("Ivan");
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post().then()
                .statusCode(201);
        //удаление добавленного студента(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .when().delete().then()
                .statusCode(200);
    }

    @Test
    @SneakyThrows
    public void Test8_DeleteStudentWithWrongId() {
        //удаление студента с некорректным id
        int id = -1;
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .when().delete().then()
                .statusCode(404);
    }

    @Test
    @SneakyThrows
    public void Test9_GetTopStudentEmptyBase() throws JsonProcessingException {
        //проверка topStudent на пустой базе
        RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .when().get().then()
                .statusCode(200);
    }

    @Test
    @SneakyThrows
    public void Test10_GetTopStudentWithNoMarks() throws JsonProcessingException {
        //добавление студента 1 без оценок
        Student st1 = new Student();
        st1.setId(1);
        st1.setName("Ivan");
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post().then()
                .statusCode(201);
        //добавление студента 2 без оценок
        Student st2 = new Student();
        st2.setId(2);
        st2.setName("Pete");
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st2))
                .when().post().then()
                .statusCode(201);
        //проверка topStudent на студентах без оценок
        RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .when().get().then()
                .statusCode(200);

        //удаление добавленного студента 1(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + st1.getId())
                .when().delete().then()
                .statusCode(200);

        //удаление добавленного студента 2(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + st2.getId())
                .when().delete().then()
                .statusCode(200);

    }

    @Test
    @SneakyThrows
    public void Test11_1_GetTopStudentWithMarks() throws JsonProcessingException {
        //добавление 1 студента (с максимальной средней оценкой)
        Student st1 = new Student();
        st1.setId(1);
        st1.setName("Ivan");
        List marks = new ArrayList<>();
        marks.add(5);
        marks.add(5);
        st1.setMarks(marks);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post().then()
                .statusCode(201);
        //добавление 2 студента
        Student st2 = new Student();
        st2.setId(2);
        st2.setName("Pete");
        List marks2 = new ArrayList<>();
        marks2.add(3);
        marks2.add(3);
        st2.setMarks(marks2);
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st2))
                .when().post().then()
                .statusCode(201);
        //проверка topStudent на наличие 1 студента c максимальной средней
        JsonPath jp = RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .when().get().then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue())
                .extract().jsonPath();

        Assertions.assertEquals(1,jp.getList("id").size());
        Assertions.assertEquals(1,jp.getList("id").get(0));
        Assertions.assertEquals(st1.getName(),jp.getList("name").get(0));
        Assertions.assertEquals(st1.getMarks(),jp.getList("marks").get(0));

        //удаление добавленного студента 1(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + st1.getId())
                .when().delete().then()
                .statusCode(200);
        //удаление добавленного студента 2(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + st2.getId())
                .when().delete().then()
                .statusCode(200);
    }

    @Test
    @SneakyThrows
    public void Test11_2_GetTopStudentWithMarks() throws JsonProcessingException {
        //добавление 1 студента (с максимальной средней оценкой)
        Student st1 = new Student();
        st1.setId(1);
        st1.setName("Ivan");
        List marks = new ArrayList<>();
        marks.add(5);
        marks.add(5);
        marks.add(5);
        st1.setMarks(marks);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post().then()
                .statusCode(201);
        //добавление 2 студента
        Student st2 = new Student();
        st2.setId(2);
        st2.setName("Pete");
        List marks2 = new ArrayList<>();
        marks2.add(5);
        marks2.add(5);
        st2.setMarks(marks2);
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st2))
                .when().post().then()
                .statusCode(201);
        //проверка topStudent на наличие 1 студента c максимальной средней (среди всех студентов с максимальной средней у него их больше всего)
        JsonPath jp = RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .when().get().then()
                .statusCode(200)
                .extract().jsonPath();
        Assertions.assertEquals(1,jp.getList("id").size());
        Assertions.assertEquals(1,jp.getList("id").get(0));
        Assertions.assertEquals(st1.getName(),jp.getList("name").get(0));
        Assertions.assertEquals(st1.getMarks(),jp.getList("marks").get(0));


        //удаление добавленного студента 1(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + st1.getId())
                .when().delete().then()
                .statusCode(200);
        //удаление добавленного студента 2(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + st2.getId())
                .when().delete().then()
                .statusCode(200);
    }

    @Test
    @SneakyThrows
    public void Test12_GetTopStudentWithNoMarks() throws JsonProcessingException {
        //добавление 1 студента (с максимальной средней оценкой)
        Student st1 = new Student();
        st1.setId(1);
        st1.setName("Ivan");
        List marks = new ArrayList<>();
        marks.add(5);
        marks.add(5);
        marks.add(5);
        st1.setMarks(marks);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st1))
                .when().post().then()
                .statusCode(201);
        //добавление 2 студента
        Student st2 = new Student();
        st2.setId(2);
        st2.setName("Pete");
        List marks2 = new ArrayList<>();
        marks2.add(5);
        marks2.add(5);
        marks2.add(5);
        st2.setMarks(marks2);
        RestAssured.given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st2))
                .when().post().then()
                .statusCode(201);
        //проверка topstudent на наличие 2 студентов c максимальной средней
        JsonPath jp = RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .when().get().then()
                .statusCode(200)
                .extract().jsonPath();

        Assertions.assertEquals(2,jp.getList("id").size());
        Assertions.assertEquals(1,jp.getList("id").get(0));
        Assertions.assertEquals(st1.getName(),jp.getList("name").get(0));
        Assertions.assertEquals(st1.getMarks(),jp.getList("marks").get(0));
        Assertions.assertEquals(2,jp.getList("id").get(1));
        Assertions.assertEquals(st2.getName(),jp.getList("name").get(1));
        Assertions.assertEquals(st2.getMarks(),jp.getList("marks").get(1));

       //удаление добавленного студента 1(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/"+st1.getId())
                .when().delete().then()
                .statusCode(200);
        //удаление добавленного студента 2(для обеспечения независимости тестов между собой)
        RestAssured.given()
                .baseUri("http://localhost:8080/student/"+st2.getId())
                .when().delete().then()
                .statusCode(200);
    }

}


