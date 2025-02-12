package propensi.amesta.model.EndUser;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table; 

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("general_manager") 
@Table(name = "general_manager")
public class GeneralManager extends User{
    
}