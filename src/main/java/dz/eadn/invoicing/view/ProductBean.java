package dz.eadn.invoicing.view;

import dz.eadn.invoicing.model.Product;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


@Named
@Stateful
@ViewScoped
public class ProductBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Product entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Product product;

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@PersistenceContext(unitName = "ADMIN_PU")
	private EntityManager entityManager;

	@Inject
	private Event<Product> productEvent;

	public String create() {

		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.product = this.example;
		} else {
			this.product = findById(getId());
		}
	}

	public Product findById(Long id) {

		return this.entityManager.find(Product.class, id);
	}

	/*
	 * Support updating and deleting Product entities
	 */

	public String update() {

		try {
			if (this.id == null) {
				this.entityManager.persist(this.product);
				productEvent.fire(product);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.product);
				return "view?faces-redirect=true&id=" + this.product.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {

		try {
			Product deletableEntity = findById(getId());

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
	 * Support searching Product entities with pagination
	 */

	private int page;
	private long count;
	private List<Product> pageItems;

	private Product example = new Product();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Product getExample() {
		return this.example;
	}

	public void setExample(Product example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Product> root = countCriteria.from(Product.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
		root = criteria.from(Product.class);
		TypedQuery<Product> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Product> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String Name = this.example.getName();
		if (Name != null && !"".equals(Name)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("name")),
					'%' + Name.toLowerCase() + '%'));
		}
		String Designation = this.example.getDesignation();
		if (Designation != null && !"".equals(Designation)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("designation")),
					'%' + Designation.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Product> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Product entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Product> getAll() {

		CriteriaQuery<Product> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(Product.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Product.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final ProductBean ejbProxy = this.sessionContext
				.getBusinessObject(ProductBean.class);

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

				return String.valueOf(((Product) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Product add = new Product();

	public Product getAdd() {
		return this.add;
	}

	public Product getAdded() {
		Product added = this.add;
		this.add = new Product();
		return added;
	}
}
