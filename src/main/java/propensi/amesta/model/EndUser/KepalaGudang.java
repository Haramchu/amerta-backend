package propensi.amesta.model.EndUser;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table; 

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("kepala_gudang") 
@Table(name = "kepala_gudang")
public class KepalaGudang extends User{
    
}