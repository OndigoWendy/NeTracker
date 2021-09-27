package models.dao;

import models.DepartmentNews;
import models.Department;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;

import static org.junit.Assert.*;

public class Sql2ODepartmentDaoTest {
    private Connection conn;
    private Sql2ODepartmentDao restaurantDao;
    private Sql2ODepartmentNewsDao foodtypeDao;
    private Sql2ONewsDetailsDao reviewDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        restaurantDao = new Sql2ODepartmentDao(sql2o);
        foodtypeDao = new Sql2ODepartmentNewsDao(sql2o);
        reviewDao = new Sql2ONewsDetailsDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingFoodSetsId() throws Exception {
        Department testDepartment = setupRestaurant();
        int originalRestaurantId = testDepartment.getId();
        restaurantDao.add(testDepartment);
        assertNotEquals(originalRestaurantId, testDepartment.getId());
    }

    @Test
    public void addedRestaurantsAreReturnedFromGetAll() throws Exception {
        Department testDepartment = setupRestaurant();
        assertEquals(1, restaurantDao.getAll().size());
    }

    @Test
    public void noRestaurantsReturnsEmptyList() throws Exception {
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void findByIdReturnsCorrectRestaurant() throws Exception {
        Department testDepartment = setupRestaurant();
        Department otherDepartment = setupRestaurant();
        assertEquals(testDepartment, restaurantDao.findById(testDepartment.getId()));
    }

    @Test
    public void updateCorrectlyUpdatesAllFields() throws Exception {
        Department testDepartment = setupRestaurant();
        restaurantDao.update(testDepartment.getId(), "a", "b", "c", "d", "e", "f");
        Department foundDepartment = restaurantDao.findById(testDepartment.getId());
        assertEquals("a", foundDepartment.getName());
        assertEquals("b", foundDepartment.getCompany());
        assertEquals("c", foundDepartment.getDivision());
        assertEquals("d", foundDepartment.getPhone());
        assertEquals("e", foundDepartment.getWebsite());
        assertEquals("f", foundDepartment.getEmail());
    }

    @Test
    public void deleteByIdDeletesCorrectRestaurant() throws Exception {
        Department testDepartment = setupRestaurant();
        restaurantDao.deleteById(testDepartment.getId());
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Department testDepartment = setupRestaurant();
        Department otherDepartment = setupRestaurant();
        restaurantDao.clearAll();
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void getAllFoodtypesForARestaurantReturnsFoodtypesCorrectly() throws Exception {
        DepartmentNews testDepartmentNews = new DepartmentNews("Seafood");
        foodtypeDao.add(testDepartmentNews);

        DepartmentNews otherDepartmentNews = new DepartmentNews("Bar Food");
        foodtypeDao.add(otherDepartmentNews);

        Department testDepartment = setupRestaurant();
        restaurantDao.add(testDepartment);
        restaurantDao.addDepartmentToDepartmentNews(testDepartment, testDepartmentNews);
        restaurantDao.addDepartmentToDepartmentNews(testDepartment, otherDepartmentNews);

        DepartmentNews[] departmentNews = {testDepartmentNews, otherDepartmentNews}; //oh hi what is this?

        assertEquals(Arrays.asList(departmentNews), restaurantDao.getAllFoodtypesByRestaurant(testDepartment.getId()));
    }

    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
        DepartmentNews testDepartmentNews = new DepartmentNews("Seafood");
        foodtypeDao.add(testDepartmentNews);

        Department testDepartment = setupRestaurant();
        restaurantDao.add(testDepartment);

        Department altDepartment = setupAltRestaurant();
        restaurantDao.add(altDepartment);

        restaurantDao.addDepartmentToDepartmentNews(testDepartment, testDepartmentNews);
        restaurantDao.addDepartmentToDepartmentNews(altDepartment, testDepartmentNews);

        restaurantDao.deleteById(testDepartment.getId());
        assertEquals(0, restaurantDao.getAllFoodtypesByRestaurant(testDepartment.getId()).size());
    }


    //helpers

    public Department setupRestaurant (){
        Department department = new Department("Fish Omena", "214 NE Ngara", "97232", "254-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(department);
        return department;
    }

    public Department setupAltRestaurant (){
        Department department = new Department("Fish Omena", "214 NE Ngara", "97232", "254-402-9874");
        restaurantDao.add(department);
        return department;
    }
}