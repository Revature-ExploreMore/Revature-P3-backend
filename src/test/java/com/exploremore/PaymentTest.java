package com.exploremore;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.exploremore.dao.PaymentDao;
import com.exploremore.entity.PaymentEntity;
import com.exploremore.exceptions.GlobalException;
import com.exploremore.pojo.PaymentPojo;
import com.exploremore.service.PaymentServiceImpl;


@ExtendWith(MockitoExtension.class)
public class PaymentTest{
	
	@Mock
	PaymentDao paymentDao;
	

	@InjectMocks
	PaymentServiceImpl paymentService;

	
	
	private PaymentPojo expectedPaymentPojo;
	private PaymentEntity dummyPaymentEntity;
	Date currentDate = Date.valueOf("2022-06-24");
	
	@BeforeEach
    public void setup(){
        
		expectedPaymentPojo = new PaymentPojo(1, "credit", "1234567899876543", currentDate, 908, 3);
        dummyPaymentEntity = new PaymentEntity(1, "credit", "1234567899876543", currentDate, 908, 3);
//        dummyBillingEntity = new BillingEntity(1, "Darnborough way", "Toronto", "Ontario", 503111, 2);
        
    }
	
	@DisplayName("JUnit test for save addPayment method")
    @Test
    public void testAddPayment() throws GlobalException{
       when(paymentDao.saveAndFlush(any(PaymentEntity.class))).thenReturn(dummyPaymentEntity);
		

       PaymentPojo sendPaymentPojo = new PaymentPojo(1, "credit", "1234567899876543", currentDate, 908, 3);
       PaymentPojo actualPaymentPojo = paymentService.addPaymentInfo(sendPaymentPojo);

       assertEquals(1, actualPaymentPojo.getId());
    }
	@DisplayName("JUnit test for save GetPayment method")
	@Test
	public void testGetPayment() throws GlobalException {
		when(paymentDao.findByUserId(3)).thenReturn(List.of(dummyPaymentEntity, dummyPaymentEntity));
		List<PaymentPojo> actualAllPaymentPojoList = paymentService.getPaymentInfo(3);
    	
    	assertNotNull(actualAllPaymentPojoList);
    	assertEquals(2, actualAllPaymentPojoList.size());
	}



}
