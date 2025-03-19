package propensi.amesta.model.EndUser;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED) 
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING) 
@Table(name = "end_user")
public class User {

    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", insertable = false, updatable = false)
    private String role;

    @NotNull
    @Size(max = 100)
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Size(max = 100)
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Size(max = 50)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "gender", nullable = false)
    private boolean gender;

    @Size(max = 20)
    @Column(name = "phone")
    private String phone;

    @Size(max = 20)
    @Column(name = "home_phone")
    private String homePhone;

    @Size(max = 20)
    @Column(name = "business_phone")
    private String businessPhone;

    @Size(max = 20)
    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    @NotNull
    @Column(name = "entry_date")
    @Temporal(TemporalType.DATE)
    private Date entryDate;

    @NotNull
    @Size(max = 50)
    @Column(name = "ktp_number")
    private String ktpNumber;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", updatable = false, nullable = false)
    private Date createdDate;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    public String getRole() {
        return this.role;
    }
}