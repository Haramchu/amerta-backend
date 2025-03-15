package propensi.amesta.model.EndUser;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table; 

import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.Aset.Gudang;

@Getter
@Setter
@Entity
@DiscriminatorValue("kepala_gudang") 
@Table(name = "kepala_gudang")
public class KepalaGudang extends User{
    
    @OneToMany(mappedBy = "kepalaGudang", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Gudang> listGudang;    

}