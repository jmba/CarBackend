package com.udacity.vehicles.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.vehicles.api.responses.CarsResponse;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        ResultActions resultActions = mvc.perform(get("/cars")).andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        CarsResponse response = objectMapper.readValue(contentAsString, CarsResponse.class);
        Car car = response._embedded.carList.get(0);
        Car compareCar = getCar();
        compareCars(car, compareCar);
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        ResultActions resultActions = mvc.perform(get("/cars/" + 1)).andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Car car = objectMapper.readValue(contentAsString, Car.class);
        assertThat(car, is(notNullValue()));
        Car compareCar = getCar();
        compareCars(car, compareCar);
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        mvc.perform(delete("/cars/" + 1)).andExpect(status().isNoContent());
    }

    /**
     * Tests the update a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void updateCar() throws Exception {
        Car car = getCar();
        car.setCondition(Condition.NEW);
        mvc.perform(
                put(new URI("/cars/1"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }

    private void compareCars(Car one, Car two){
        assertThat(one.getLocation(), samePropertyValuesAs(two.getLocation()));
        assertThat(one.getDetails().getManufacturer(), samePropertyValuesAs(two.getDetails().getManufacturer()));
        assertThat(one.getDetails().getModel(), samePropertyValuesAs(two.getDetails().getModel()));
        assertThat(one.getDetails().getMileage(), samePropertyValuesAs(two.getDetails().getMileage()));
        assertThat(one.getDetails().getExternalColor(), samePropertyValuesAs(two.getDetails().getExternalColor()));
        assertThat(one.getDetails().getBody(), samePropertyValuesAs(two.getDetails().getBody()));
        assertThat(one.getDetails().getEngine(), samePropertyValuesAs(two.getDetails().getEngine()));
        assertThat(one.getDetails().getFuelType(), samePropertyValuesAs(two.getDetails().getFuelType()));
        assertThat(one.getDetails().getModelYear(), samePropertyValuesAs(two.getDetails().getModelYear()));
        assertThat(one.getDetails().getProductionYear(), samePropertyValuesAs(two.getDetails().getProductionYear()));
        assertThat(one.getDetails().getNumberOfDoors(), samePropertyValuesAs(two.getDetails().getNumberOfDoors()));
    }
}