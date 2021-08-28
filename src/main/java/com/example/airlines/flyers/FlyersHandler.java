package com.example.airlines.flyers;

import com.example.airlines.Utils;
import com.example.airlines.exceptions.CustomerNotFoundException;
import com.example.airlines.exceptions.EmptyListException;
import com.example.airlines.model.Customer;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/airline")
public class FlyersHandler {

    private static  final Logger LOG = LoggerFactory.getLogger(FlyersHandler.class);
    private static volatile List<Customer> allCustomers = new ArrayList<>();
    private static volatile List<Customer> frequentFlyers = new ArrayList<>();
    private static final long FF_LOWER_LIMIT = 100000;
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    Type listOfCustomerObj = new TypeToken<ArrayList<Customer>>(){}.getType();

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/load/customers/{noOfCustomers}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loadDataRandomly(@PathVariable final int noOfCustomers) {

        if(noOfCustomers <=0 || noOfCustomers >= 100000) {
            return ResponseEntity.badRequest()
                    .body("No. of customers invalid. Please provide valid value between 0 & 100000 value");
        }

        int i = 0;
        if(CollectionUtils.isEmpty(allCustomers)) {
            allCustomers = new ArrayList<>();
        }

        while(i++ < noOfCustomers) {
            Customer customer = new Customer(Utils.generateRandomName(), Utils.getRandomNumber(98000, 103000));
            LOG.info("{} added", customer);
            allCustomers.add(customer);
        }

        return ResponseEntity.ok()
                .body("{\"status\": \"OK\"}");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/load/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loadData(@RequestBody String newCustomers) {
        List<Customer> tempCustomers;


        if(StringUtils.isEmpty(newCustomers)) {
            return ResponseEntity.badRequest()
                    .body("Input invalid");
        }

        try {
            tempCustomers = GSON.fromJson(newCustomers, listOfCustomerObj);
            LOG.info("Temp Customers: {}", tempCustomers);
        } catch (Throwable t) {
            return ResponseEntity.badRequest()
                    .body("Input invalid");
        }

        int i = 0;
        if(CollectionUtils.isEmpty(allCustomers)) {
            allCustomers = new ArrayList<>();
        }

        allCustomers.addAll(tempCustomers);

        return ResponseEntity.ok()
                .body("{\"status\": \"OK\"}");
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    // Here we can add authorization
    //@PreAuthorize("isAuthorized(#headers)")
    @GetMapping(value = "/frequentflyers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Customer> getFrequentFlyers() {

        if (CollectionUtils.isEmpty(allCustomers)) {
            throw new EmptyListException("Collection of customers is empty");
        }

        long startTime = System.currentTimeMillis();
        frequentFlyers = getFrequentFlyers(allCustomers);
        LOG.info("Frequent flyers: {}", frequentFlyers.size());
        LOG.info("Time taken to get frequent flyers processingTimeMs={}", (System.currentTimeMillis() - startTime));

        if(CollectionUtils.isEmpty(frequentFlyers)) {
            throw new CustomerNotFoundException("Did not find any frequent flyers");
        }

        return frequentFlyers;
    }

    @ResponseStatus(HttpStatus.OK)
    // Here we can add authorization
    //@PreAuthorize("isAuthorized(#headers)")
    @DeleteMapping(value = "/customers/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCustomerData() {
        allCustomers = null;
        frequentFlyers = null;
        LOG.info("All customer data deleted");
        return ResponseEntity.ok("All customer data deleted");
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(value = "/frequentflyers/page/{pageNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Customer> getFrequentFlyersPage(@PathVariable int pageNumber) {

        if (CollectionUtils.isEmpty(allCustomers)) {
            throw new EmptyListException("Collection of customers is empty");
        }

        List<Customer> customerPage = getPage(frequentFlyers, pageNumber, 10);

        return customerPage;
    }



    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {

        if(pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if(sourceList == null || sourceList.size() < fromIndex){
            return Collections.emptyList();
        }
        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

    public static <T> void doPaginated(Collection<T> fullList, Integer pageSize, Page<T> pageInterface) {
        final List<T> list = new ArrayList<T>(fullList);
        if (pageSize == null || pageSize <= 0 || pageSize > list.size()){
            pageSize = list.size();
        }

        final int numPages = (int) Math.ceil((double)list.size() / (double)pageSize);
        for (int pageNum = 0; pageNum < numPages;){
            final List<T> page = list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size()));
            pageInterface.run(page);
        }
    }

    public interface Page<T> {
        void run(List<T> item);
    }

    @Cacheable
    public List<Customer> getFrequentFlyers(List<Customer> allCustomers) {
        // We can do DB query instead of in memory arrayList if list is too big
        List<Customer> frequentFlyersCustomers = allCustomers.stream().filter(customer -> {
            if(customer.getMiles()<=1) {
                return false;
            }
            if (customer.getMiles() > FF_LOWER_LIMIT) {
                LOG.info("Customer: {}", customer.getCustomerName());
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return frequentFlyersCustomers;
    }
}
