package dz.eadn.invoicing.view;

import dz.eadn.invoicing.model.Customer;
import dz.eadn.invoicing.model.OrderDetails;
import dz.eadn.invoicing.model.Product;
import dz.eadn.invoicing.model.SalesOrder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.primefaces.PrimeFaces;


@Named
@Stateful
@ViewScoped
public class OrderBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "ADMIN_PU")
	private EntityManager entityManager;
        
	/*
	 * Support creating and retrieving SalesOrder entities
	 */

	private Long id;
        
        private Date dateOrder = new Date();
        
        
        private List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
        
        private OrderDetails selectedOrderDetails;

        private List<OrderDetails> selectedOrderDetailsList = new ArrayList<OrderDetails>();    
        
        private Customer customerExample = new Customer();
        
	private SalesOrder example = new SalesOrder(customerExample);

	private SalesOrder order = new SalesOrder(customerExample, dateOrder);
        
        private Customer customer;
        
        private Product product;        
        
        private List<Customer> listCustomers = new ArrayList<Customer>();
        
        private List<Product> listProducts = new ArrayList<Product>();
        
        private int page;
	
        private long count;
	
        private List<SalesOrder> pageItems;
        
        private SalesOrder add = new SalesOrder();
        
        
        /******************* Getters & Setters *********************/
        
        public Long getId() {
                return this.id;
        }

	public void setId(Long id) {
		this.id = id;
	}

	public SalesOrder getOrder() {
		return this.order;
	}

	public void setOrder(SalesOrder order) {
		this.order = order;
	}
               

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
        
        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
                     
        
        public List<Customer> getListCustomers() {
            return this.entityManager
                      .createQuery("Select c from Customer c", Customer.class)
                      .getResultList();
        }
        
        public void setListCustomers(List<Customer> listCustomers) {
            this.listCustomers = listCustomers;
        }
              
        public List<Product> getListProducts() {
            return this.entityManager
                      .createQuery("Select p from Product p", Product.class)
                      .getResultList();
        }
        
        public void setListProducts(List<Product> listProducts) {
            this.listProducts = listProducts;
        }        
        
        public SalesOrder getAdd() {
		return this.add;
	}

	public SalesOrder getAdded() {
		SalesOrder added = this.add;
		this.add = new SalesOrder();
		return added;
	}
        

        public List<OrderDetails> getOrderDetailsList() {
                return this.entityManager
                      .createQuery("Select od from OrderDetails od", OrderDetails.class)
                      .getResultList();
        }
        
        public void setOrderDetailsList(List<OrderDetails> orderDetailsList) {
                this.orderDetailsList = orderDetailsList;
        }        

        public OrderDetails getSelectedOrderDetails() {
            return selectedOrderDetails;
        }

        public void setSelectedOrderDetails(OrderDetails selectedOrderDetails) {
            this.selectedOrderDetails = selectedOrderDetails;
        }

        public List<OrderDetails> getSelectedOrderDetailsList() {
            return selectedOrderDetailsList;
        }

        public void setSelectedOrderDetailsList(List<OrderDetails> selectedOrderDetailsList) {
            this.selectedOrderDetailsList = selectedOrderDetailsList;
        }

        public void openNew() {
            this.selectedOrderDetails = new OrderDetails();
        }

        public void saveOrderDetails() {
            
		try {
			if (this.selectedOrderDetails.getId() == null) {
                            
                            this.orderDetailsList.add(this.selectedOrderDetails);
				this.entityManager.persist(this.selectedOrderDetails);
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order Details Added"));

			} else {
				this.entityManager.merge(this.selectedOrderDetails);
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order Details Updated"));
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
		}
                               
//            if (this.selectedOrderDetails.getId()== null) {
//                //this.selectedOrderDetails.setCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9));
//                this.orderDetailsList.add(this.selectedOrderDetails);
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order Details Added"));
//            }
//            else {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order Details Updated"));
//            }
            

            PrimeFaces.current().executeScript("PF('manageOrderDetailsDialog').hide()");
            PrimeFaces.current().ajax().update("create:messages", "create:dt-orderDetailsList");
        }

        public void deleteOrderDetails() {
            	try {
			OrderDetails deletableEntity = this.entityManager.find(OrderDetails.class, this.selectedOrderDetails.getId());
			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
                        this.orderDetailsList.remove(this.selectedOrderDetails);
                        this.selectedOrderDetails = null;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order Details Removed"));
                        PrimeFaces.current().ajax().update("create:messages", "create:dt-orderDetailsList");
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
		}
        }

        public String getDeleteButtonMessage() {
            if (hasSelectedOrderDetailsList()) {
                int size = this.selectedOrderDetailsList.size();
                return size > 1 ? size + " orderDetailsList selected" : "1 orderDetails selected";
            }

            return "Delete";
        }

        public boolean hasSelectedOrderDetailsList() {
            return this.selectedOrderDetailsList != null && !this.selectedOrderDetailsList.isEmpty();
        }

        public void deleteSelectedOrderDetailsList() {
            this.orderDetailsList.removeAll(this.selectedOrderDetailsList);
            this.selectedOrderDetailsList = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order Details List Removed"));
            PrimeFaces.current().ajax().update("create:messages", "create:dt-orderDetailsList");
            PrimeFaces.current().executeScript("PF('dtOrderDetailsList').clearFilters()");
        }
        
        
        
        
        /******************* Methods *********************/


	public String create() {
            
            //this.listCustomers = this.getListCustomer();
//            this.conversation.begin();
//            this.conversation.setTimeout(1800000L);
            return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.order = this.example;
		} else {
			this.order = findById(getId());
		}
	}

	public SalesOrder findById(Long id) {

		return this.entityManager.find(SalesOrder.class, id);
	}

	/*
	 * Support updating and deleting SalesOrder entities
	 */

	public String update() {

		try {
			if (this.id == null) {
				this.entityManager.persist(this.order);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.order);
				return "view?faces-redirect=true&id=" + this.order.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}
        

	public String delete() {

		try {
			SalesOrder deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching SalesOrder entities with pagination
	 */


	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public SalesOrder getExample() {
		return this.example;
	}

	public void setExample(SalesOrder example) {
		this.example = example;
	}
        
        public Date getDateOrder() {
		return this.dateOrder;
	}

	public void setDateOrder(Date dateOrder) {
		this.dateOrder = dateOrder;
	}

	public String search() {
		this.page = 0;
		return null;
	}
        
            public void paginateOrder() {
                
                
//                CriteriaBuilder qb = entityManager.getCriteriaBuilder();
//                CriteriaQuery<Long> cq = qb.createQuery(Long.class);
//                cq.select(qb.count(cq.from(SalesOrder.class)));
//                cq.where(/*your stuff*/);
//                this.count = entityManager.createQuery(cq).getSingleResult();
            
//                String countQuery = "SELECT COUNT(*) FROM salesOrder order WHERE";           
//                String selectQuery = "SELECT order FROM salesOrder order WHERE";
//                
//                
//		String firstName = this.example.getCustomer().getFirstName();
//		if (firstName != null && !"".equals(firstName)) 
//                {
//                    countQuery += "order.customer.firstName LIKE ?1";
//                    selectQuery += "order.customer.firstName LIKE ?1";
//		}
//		Date orderDate = this.example.getOrderDate();
//		if (orderDate != null) 
//                {
//                    countQuery += "order.orderDate LIKE ?2";
//                    selectQuery += "order.orderDate LIKE ?2";
//		}
//                
//                TypedQuery<Long> typedCountQuery = this.entityManager.createQuery(countQuery, Long.class)
//                        .setParameter("1", this.example.getCustomer().getFirstName())
//                        .setParameter("2", this.example.getOrderDate());
//                this.count = typedCountQuery.getSingleResult();
//                
//                TypedQuery<SalesOrder> typedSelectQuery = this.entityManager.createQuery(selectQuery, SalesOrder.class)
//                        .setParameter("1", this.example.getCustomer().getFirstName())
//                        .setParameter("2", this.example.getOrderDate());
//                typedSelectQuery.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());            
//		this.pageItems = typedSelectQuery.getResultList();
	}

	public void paginate() {
            
            this.example.setCustomer(this.customerExample);  //******************************

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<SalesOrder> root = countCriteria.from(SalesOrder.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<SalesOrder> criteria = builder.createQuery(SalesOrder.class);
		root = criteria.from(SalesOrder.class);
		TypedQuery<SalesOrder> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<SalesOrder> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();
                        
                String customer = this.example.getCustomer().getFirstName();
                if (customer != null && !"".equals(customer)) {
                        predicatesList.add(builder.like(
                                        builder.lower(root.<String> get("customer").get("firstName")),
                                        '%' + customer.toLowerCase() + '%'));
                }
                Date orderDate = this.example.getOrderDate();
                if (orderDate != null ) 
                {
                        predicatesList.add(builder.equal(root.get("orderDate"), orderDate));   
                }

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<SalesOrder> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back SalesOrder entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<SalesOrder> getAll() {

		CriteriaQuery<SalesOrder> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(SalesOrder.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(SalesOrder.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final OrderBean ejbProxy = this.sessionContext
				.getBusinessObject(OrderBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}
				return String.valueOf(((SalesOrder) value).getId());
			}
		};
	}



}
