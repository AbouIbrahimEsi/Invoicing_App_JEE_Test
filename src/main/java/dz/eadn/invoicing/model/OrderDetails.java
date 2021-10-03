/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dz.eadn.invoicing.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author NAIMI Mohamed
 */
@Entity
@Table(name="orderdetails")
@Getter
@Setter
public class OrderDetails implements Serializable{
    
	@Id
        @Column(name = "id", updatable = false, nullable = false)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderdetails_gen")
        @SequenceGenerator(name = "orderdetails_gen", sequenceName = "orderdetails_seq", schema = "public", allocationSize = 1)
        private Long id;
        
	private static final long serialVersionUID = 1L;
        
        @ManyToOne(fetch=FetchType.LAZY)
        private Product product;

	@Column
	private Float orderPrice;
        
        @Column
	private Integer quantity;

}
