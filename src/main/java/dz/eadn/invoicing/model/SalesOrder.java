package dz.eadn.invoicing.model;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="salesOrder")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SalesOrder implements Serializable {

	@Id
        @Column(name = "id", updatable = false, nullable = false)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_gen")
        @SequenceGenerator(name = "order_gen", sequenceName = "order_seq", schema = "public", allocationSize = 1)
        private Long id;
        
	private static final long serialVersionUID = 1L;
        
        @ManyToOne(fetch=FetchType.LAZY)
        private Customer customer;
        
        @Column(nullable = false)
        @Temporal(TemporalType.DATE)
	private Date orderDate;
        
        @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
        private Set<OrderDetails> listOrderDetails = new HashSet<OrderDetails>();
                        
        @Transient
	private Float totalPrice;

        public SalesOrder(Customer customer) {
            this.customer = customer;
        }
        
        public SalesOrder(Customer customer, Date orderDate) {
            this.customer = customer;
            this.orderDate = orderDate;
        }
        
        

}