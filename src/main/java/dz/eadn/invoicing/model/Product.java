package dz.eadn.invoicing.model;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="product")
public class Product implements Serializable, Comparable<Product> {

	@Id
        @Column(name = "id", updatable = false, nullable = false)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_gen")
        @SequenceGenerator(name = "product_gen", sequenceName = "product_seq", schema = "public", allocationSize = 1)
        private Long id;
        
	private static final long serialVersionUID = 1L;

        @Column(nullable = false)
	private String name;
        
        @Column(nullable = false)
	private Float price;
        
        @Column
	private String color;
        
	@Column
	private String designation;
        
        @Override
        public int compareTo(Product p) {
            return name.compareTo(p.name);
        }


}