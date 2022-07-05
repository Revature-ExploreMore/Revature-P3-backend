package com.exploremore.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exploremore.dao.OrderCourseDao;
import com.exploremore.dao.OrderDao;
import com.exploremore.entity.CourseEntity;
import com.exploremore.entity.OrderCourseEntity;
import com.exploremore.entity.OrderEntity;
import com.exploremore.entity.UserEntity;
import com.exploremore.exceptions.EmptyOrderList;
import com.exploremore.exceptions.GlobalException;
import com.exploremore.exceptions.OrderNotFoundException;
import com.exploremore.pojo.CategoryPojo;
import com.exploremore.pojo.CoursePojo;
import com.exploremore.pojo.OrderCoursePojo;
import com.exploremore.pojo.OrderCourseSet;
import com.exploremore.pojo.OrderPojo;
import com.exploremore.pojo.UserPojo;
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderDao orderDao;

	final static Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

	
	@Autowired
	OrderCourseDao orderCourseDao;


	@Override
	public OrderPojo addOrder(OrderPojo orderPojo) throws GlobalException {
		OrderEntity orderEntity = new OrderEntity();
		BeanUtils.copyProperties(orderPojo, orderEntity);
		
		UserEntity userEnt = new UserEntity();
		BeanUtils.copyProperties(orderPojo.getUser(), userEnt);
		orderEntity.setUser(userEnt);
		
		
		OrderEntity returnedOrderEntity = orderDao.saveAndFlush(orderEntity);
		orderPojo.setId(returnedOrderEntity.getId());
		return orderPojo;
	}

	@Override
	public List<OrderPojo> viewAllOrders() throws GlobalException, EmptyOrderList {
		List<OrderEntity> allOrderEntity = orderDao.findAll();
		List<OrderPojo> allOrderPojo = new ArrayList<OrderPojo>();

		if (allOrderEntity.isEmpty()) {
			throw new EmptyOrderList();
		} else {
			for (OrderEntity fetchedOrderEntity : allOrderEntity) {
				UserPojo user = new UserPojo();
				BeanUtils.copyProperties(fetchedOrderEntity.getUser(), user);
				OrderPojo returnedOrderPojo = new OrderPojo(fetchedOrderEntity.getId(),
						fetchedOrderEntity.getOrderTimestamp(), fetchedOrderEntity.getOrderTotal(),user);

				allOrderPojo.add(returnedOrderPojo);
			}
		}
		return allOrderPojo;
	}

	@Override
	public List<OrderPojo> viewOrderById(int id) throws GlobalException, OrderNotFoundException {
		List<OrderEntity> searchOrderEntity = orderDao.findById(id);
		List<OrderPojo> searchOrderPojo = new ArrayList<OrderPojo>();

		if (searchOrderEntity.isEmpty()) {
			throw new OrderNotFoundException(id);
		} else {
			for (OrderEntity fetchedOrderEntity : searchOrderEntity) {
				UserPojo user = new UserPojo();
				BeanUtils.copyProperties(fetchedOrderEntity.getUser(), user);
				OrderPojo returnOrderPojo = new OrderPojo(fetchedOrderEntity.getId(),
						fetchedOrderEntity.getOrderTimestamp(), fetchedOrderEntity.getOrderTotal(),
						user);

				searchOrderPojo.add(returnOrderPojo);
			}
		}
		return searchOrderPojo;
	}
	@Override
	public List<OrderCoursePojo> getUserOrders(int userId) {
		List<OrderCourseEntity> ordCourEnts = new ArrayList<>();
		ordCourEnts = orderCourseDao.findOrderCourseByUser(userId);
		
		List<OrderCoursePojo> ordCourPojos = new ArrayList<>();
		for (OrderCourseEntity ordCourEnt : ordCourEnts) {
			OrderCoursePojo ordCourPojo = new OrderCoursePojo();
			BeanUtils.copyProperties(ordCourEnt, ordCourPojo);
			CoursePojo coursePojo = new CoursePojo();
			BeanUtils.copyProperties(ordCourEnt.getCourse(), coursePojo);
			ordCourPojo.setCourse(coursePojo);
			OrderPojo orderPojo = new OrderPojo();
			BeanUtils.copyProperties(ordCourEnt.getOrder(), orderPojo);
			UserPojo userPojo = new UserPojo();
			BeanUtils.copyProperties(ordCourEnt.getOrder().getUser(), userPojo);
			orderPojo.setUser(userPojo);
			ordCourPojo.setOrder(orderPojo);
			CategoryPojo catPojo = new CategoryPojo();
			BeanUtils.copyProperties(ordCourEnt.getCourse().getCategory(), catPojo);
			ordCourPojo.getCourse().setCategoryId(catPojo);
			
			ordCourPojos.add(ordCourPojo);
		
		}
		
		return ordCourPojos;
	}
	
	@Override
	public Integer addCoursesToOrder(OrderCourseSet ordCourseSet) throws GlobalException {
		OrderEntity ordEntity = new OrderEntity();
		BeanUtils.copyProperties(ordCourseSet.getOrder(), ordEntity);
		ordEntity.setOrderCourses(new HashSet<OrderCourseEntity>());
		UserEntity userEnt = new UserEntity();
		BeanUtils.copyProperties(ordCourseSet.getOrder().getUser(), userEnt);
		ordEntity.setUser(userEnt);
		OrderPojo orderPojo = addOrder(ordCourseSet.getOrder());
		ordEntity.setId(orderPojo.getId());
		for (CoursePojo course : ordCourseSet.getCourses()) {
			OrderCourseEntity ordCourseEnt = new OrderCourseEntity();
			CourseEntity courseEnt = new CourseEntity();
			BeanUtils.copyProperties(course, courseEnt);
			ordCourseEnt.setCourse(courseEnt);
			ordCourseEnt.setOrder(ordEntity);
			ordEntity.getOrderCourses().add(ordCourseEnt);
		}
		orderDao.saveAndFlush(ordEntity);
		return ordEntity.getId();
	}


}
