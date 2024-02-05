package com.dinedynamo.controllers;


import com.dinedynamo.api.ApiResponse;
import com.dinedynamo.collections.Reservation;
import com.dinedynamo.dto.CheckExistingInReservationOrWaitingRequest;
import com.dinedynamo.dto.ReservationOrWaitingResponseBody;
import com.dinedynamo.repositories.TableReservationRepository;
import com.dinedynamo.services.TableReservationService;
import com.dinedynamo.services.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class TableReservationController
{

    @Autowired
    TableReservationRepository tableReservationRepository;

    @Autowired
    TableReservationService tableReservationService;

    @Autowired
    TableService tableService;


    /**
     *
     * @param reservation
     * @return ApiResponse.data will have true if table is reserved else false
     */
    @PostMapping("/dinedynamo/customer/reserve-table")
    ResponseEntity<ApiResponse> reserveTable(@RequestBody Reservation reservation){

        boolean isReservationRequestValid = tableReservationService.validateReservationRequest(reservation);

        if(!isReservationRequestValid){

            throw new RuntimeException("REQUEST DOES NOT CONTAIN REQUIRED FIELDS FOR RESERVATION");
        }


        boolean isReservationPossible = tableReservationService.save(reservation);

        if(!isReservationPossible){
            System.out.println("TABLE NOT RESERVED");
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"success",new ReservationOrWaitingResponseBody("","UNRESERVED")),HttpStatus.OK);
        }


        System.out.println("IS TABLE RESERVED: "+isReservationPossible);

        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"success",new ReservationOrWaitingResponseBody(reservation,"RESERVED")),HttpStatus.OK);

    }

    @PostMapping("/dinedynamo/customer/unreserve-table")
    ResponseEntity<ApiResponse> unreserveTable(@RequestBody Reservation reservation){
        String reservationId = reservation.getReservationId();

        if(reservationId.equals(" ") || reservationId.equals("") || reservationId == null){
            System.out.println("RESERVATION-ID IS EMPTY OR NULL IN REQUEST");
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"success",new ReservationOrWaitingResponseBody(null,"INVALID_REQUEST")),HttpStatus.OK);

        }


        tableReservationRepository.delete(reservation);
        System.out.println("RESERVATION DELETED FROM DB");
        return new ResponseEntity<ApiResponse>(new ApiResponse(HttpStatus.OK,"success",new ReservationOrWaitingResponseBody(reservation,"UNRESERVED")),HttpStatus.OK);

    }


    @PostMapping("/dinedynamo/customer/check-reservation")
    public ResponseEntity<ApiResponse> isUserAlreadyReserved(@RequestBody CheckExistingInReservationOrWaitingRequest checkExistingInReservationOrWaitingRequest){


        if(checkExistingInReservationOrWaitingRequest.getCustomerPhone() == null || checkExistingInReservationOrWaitingRequest.getCustomerPhone().isEmpty()
            || checkExistingInReservationOrWaitingRequest.getRestaurantId() == null || checkExistingInReservationOrWaitingRequest.getRestaurantId().isEmpty()
        ){
            System.out.println("RESTAURANT-ID OR CUSTOMER-PHONE EMPTY IN REQUEST BODY");
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_ACCEPTABLE,"success",null),HttpStatus.OK);

        }


        Reservation reservation = tableReservationService.existsInReservationCollection(checkExistingInReservationOrWaitingRequest.getRestaurantId(), checkExistingInReservationOrWaitingRequest.getCustomerPhone());

        if(reservation == null){
            System.out.println("NO EXISTING RESERVATIONS FOR THIS PHONE-NO.");
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"success",null),HttpStatus.OK);

        }

        System.out.println("RESERVATION EXISTS ALREADY FOR THIS PHONE-NO.");
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"success",reservation),HttpStatus.OK);

    }


}
