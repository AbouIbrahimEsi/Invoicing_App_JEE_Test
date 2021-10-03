package dz.eadn.invoicing.view;

import dz.eadn.invoicing.model.Customer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.PrimeFaces;


@Named
@Stateful
@ViewScoped
public class CustomerBean implements Serializable {

	private static final long serialVersionUID = 1L;

        
        @PersistenceContext(unitName = "ADMIN_PU")
	private EntityManager entityManager;


	@Inject
	private Event<Customer> customerEvent;
        
        @Resource
	private SessionContext sessionContext;
        
	//Variables

	private Long id;

        private Customer customer;
        
        private Customer selectedCustomer = new Customer();
        
        private int page;

	private Customer example = new Customer();
        
        private Customer add = new Customer();
        
        private List<Customer> selectedCustomerList;
        
        private List<Customer> customerList = new ArrayList<Customer>();


        
        
        //Getters & Setters
        
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
        
        
        public Customer getSelectedCustomer() {
            return selectedCustomer;
        }

        public void setSelectedCustomer(Customer selectedCustomer) {
            this.selectedCustomer = selectedCustomer;
        }
        
        public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Customer getExample() {
		return this.example;
	}

	public void setExample(Customer example) {
		this.example = example;
	}
        
        public Customer getAdd() {
		return this.add;
	}

	public Customer getAdded() {
		Customer added = this.add;
		this.add = new Customer();
		return added;
	}
        
        public List<Customer> getSelectedCustomerList() {
            return selectedCustomerList;
        }

        public void setSelectedCustomerList(List<Customer> selectedCustomerList) {
            this.selectedCustomerList = selectedCustomerList;
        }
        
        public List<Customer> getCustomerList() {
                return this.entityManager
                      .createQuery("Select c from Customer c", Customer.class)
                      .getResultList();
        }

        public void setCustomerList(List<Customer> customerList) {
            this.customerList = customerList;
        }
        

                
        
        
        
        //Methods
        
        public void openNew() {
            //this.selectedCustomer = new Customer();
        }
        
        public String getDeleteButtonMessage() {
            if (hasSelectedCustomerList()) {
                int size = this.selectedCustomerList.size();
                return size > 1 ? size + " Customers List selected" : "1 Customer selected";
            }

            return "Delete";
        }
        
        public boolean hasSelectedCustomerList() {
            return this.selectedCustomerList != null && !this.selectedCustomerList.isEmpty();
        }
        
        public void deleteSelectedCustomerList() {
            this.customerList.removeAll(this.selectedCustomerList);
            this.selectedCustomerList = null;
            
//                try {
//			Customer deletableEntity = this.entityManager.find(Customer.class, this.selectedCustomer.getId());
//			this.entityManager.remove(deletableEntity);
//			this.entityManager.flush();
//                        this.customerList.remove(this.selectedCustomer);
//                        this.selectedCustomer = null;
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Customer Removed"));
//                        PrimeFaces.current().ajax().update("search:messages", "search:dt-customerList");
//		} catch (Exception e) {
//			FacesContext.getCurrentInstance().addMessage(null,
//					new FacesMessage(e.getMessage()));
//		}
            
            
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Customer List Removed"));
            PrimeFaces.current().ajax().update("search:messages", "search:dt-CustomerList");
            PrimeFaces.current().executeScript("PF('dtCustomerList').clearFilters()");
        }
        
        
            public String saveCustomer() {
                
                FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (this.customer.getId() == null) {
                            
                            if( this.findByEmail(this.customer.getMail()) != null ){
                                context.addMessage(null, new FacesMessage("Customer Exist Déja !!!"));
                                context.getExternalContext().getFlash().setKeepMessages(true);   
                            }
                            else {
                                this.customerList.add(this.customer);
                                this.entityManager.persist(this.customer);                                                         
                                context.addMessage(null, new FacesMessage("Customer Added"));
                                context.getExternalContext().getFlash().setKeepMessages(true); 
                            }


			} else {
                            this.entityManager.merge(this.customer);
                            context.addMessage(null, new FacesMessage("Customer Updated"));
                            context.getExternalContext().getFlash().setKeepMessages(true);
			}
		} catch (Exception e) {
                            context.addMessage(null, new FacesMessage(e.getMessage()));
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            
		}
             PrimeFaces.current().ajax().update("create:messages");                             
            //PrimeFaces.current().executeScript("PF('manageCustomerDialog').hide()");          
            
            return "search.xhtml?faces-redirect=true";  //Added
        }
            
            
        public void deleteCustomer() {
            	try {
			Customer deletableEntity = this.entityManager.find(Customer.class, this.selectedCustomer.getId());
			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
                        this.customerList.remove(this.selectedCustomer);
                        this.selectedCustomer = null;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Customer Removed"));
                        PrimeFaces.current().ajax().update("search:messages", "search:dt-customerList");
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
		}
        }
        
        
        public void retrieve() {
            
            System.out.println("customer selected Id: " + this.id);
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.customer = this.example;
		} else {
			this.customer = findById(getId());
		}
	}

	public Customer findById(Long id) {

		return this.entityManager.find(Customer.class, id);
	}
        
        public Customer findByEmail(String mail) {             
                               
        List<Customer> results = this.entityManager
                      .createQuery("Select c from Customer c where c.mail like ?1", Customer.class)
                      .setParameter("1", mail)
                      .getResultList();
        if (results.isEmpty()) return null;
        else if (results.size() == 1) return results.get(0);

	}
        
        
        
        
        
        
        
        
        
        
        
        
        
//	public String create() {
//
//		return "create?faces-redirect=true";
//	}
//
//	public void retrieve() {
//
//		if (FacesContext.getCurrentInstance().isPostback()) {
//			return;
//		}
//
//		if (this.id == null) {
//			this.customer = this.example;
//		} else {
//			this.customer = findById(getId());
//		}
//	}
//
//	public Customer findById(Long id) {
//
//		return this.entityManager.find(Customer.class, id);
//	}
//
//
//	public String update() {
//
//		try {
//			if (this.id == null) {
//				this.entityManager.persist(this.customer);
//				customerEvent.fire(customer);
//				return "search?faces-redirect=true";
//			} else {
//				this.entityManager.merge(this.customer);
//				return "view?faces-redirect=true&id=" + this.customer.getId();
//			}
//		} catch (Exception e) {
//			FacesContext.getCurrentInstance().addMessage(null,
//					new FacesMessage(e.getMessage()));
//			return null;
//		}
//	}
//
//	public String delete() {
//
//		try {
//			Customer deletableEntity = findById(getId());
//
//			this.entityManager.remove(deletableEntity);
//			this.entityManager.flush();
//			return "search?faces-redirect=true";
//		} catch (Exception e) {
//			FacesContext.getCurrentInstance().addMessage(null,
//					new FacesMessage(e.getMessage()));
//			return null;
//		}
//	}
//
//	/*
//	 * Support searching Customer entities with pagination
//	 */
//
//
//
//	public String search() {
//		this.page = 0;
//		return null;
//	}
//
//	public void paginate() {
//
//		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
//
//		// Populate this.count
//
//		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
//		Root<Customer> root = countCriteria.from(Customer.class);
//		countCriteria = countCriteria.select(builder.count(root)).where(
//				getSearchPredicates(root));
//		this.count = this.entityManager.createQuery(countCriteria)
//				.getSingleResult();
//
//		// Populate this.pageItems
//
//		CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
//		root = criteria.from(Customer.class);
//		TypedQuery<Customer> query = this.entityManager.createQuery(criteria
//				.select(root).where(getSearchPredicates(root)));
//		query.setFirstResult(this.page * getPageSize()).setMaxResults(
//				getPageSize());
//		this.pageItems = query.getResultList();
//	}
//
//	private Predicate[] getSearchPredicates(Root<Customer> root) {
//
//		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
//		List<Predicate> predicatesList = new ArrayList<Predicate>();
//
//		String firstName = this.example.getFirstName();
//		if (firstName != null && !"".equals(firstName)) {
//			predicatesList.add(builder.like(
//					builder.lower(root.<String> get("firstName")),
//					'%' + firstName.toLowerCase() + '%'));
//		}
//		String lastName = this.example.getLastName();
//		if (lastName != null && !"".equals(lastName)) {
//			predicatesList.add(builder.like(
//					builder.lower(root.<String> get("lastName")),
//					'%' + lastName.toLowerCase() + '%'));
//		}
//
//		return predicatesList.toArray(new Predicate[predicatesList.size()]);
//	}
//
//	public List<Customer> getPageItems() {
//		return this.pageItems;
//	}
//
//	public long getCount() {
//		return this.count;
//	}
//        
//        
//
//	/*
//	 * Support listing and POSTing back Customer entities (e.g. from inside an
//	 * HtmlSelectOneMenu)
//	 */
//
//	public List<Customer> getAll() {
//
//		CriteriaQuery<Customer> criteria = this.entityManager.getCriteriaBuilder()
//				.createQuery(Customer.class);
//		return this.entityManager.createQuery(
//				criteria.select(criteria.from(Customer.class))).getResultList();
//	}
//
//
//	public Converter getConverter() {
//
//		final CustomerBean ejbProxy = this.sessionContext
//				.getBusinessObject(CustomerBean.class);
//
//		return new Converter() {
//
//			@Override
//			public Object getAsObject(FacesContext context,
//					UIComponent component, String value) {
//
//				return ejbProxy.findById(Long.valueOf(value));
//			}
//
//			@Override
//			public String getAsString(FacesContext context,
//					UIComponent component, Object value) {
//
//				if (value == null) {
//					return "";
//				}
//
//				return String.valueOf(((Customer) value).getId());
//			}
//		};
//	}


}
