package propensi.amesta.model.EndUser;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table; 

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("direktur") 
@Table(name = "direktur")
public class Direktur extends User{
    
}