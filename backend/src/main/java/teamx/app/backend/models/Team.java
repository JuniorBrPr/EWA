package teamx.app.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import teamx.app.backend.utils.DTO.TeamDTO;
import teamx.app.backend.utils.Model;

import java.util.List;

/**
 * Team entity
 * Represents a team
 *
 * @author Joey van der Poel
 * @author Junior Javier Brito Perez
 * @see User
 */
@Data
@Builder
@Entity(name = "Teams")
@NoArgsConstructor
@AllArgsConstructor
public class Team implements Model<TeamDTO> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    private String name;

    @ManyToOne
    @JsonIgnore
    @NotNull(message = "Warehouse is required")
    private Warehouse warehouse;

    // TODO: Can a team have multiple leaders?
    @ManyToOne
    @JsonIgnore
    private User leader;

    @JsonIgnore
    @OneToMany
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "team_id")
    private List<User> members;

    @JsonIgnore
    @OneToMany
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "team_id")
    private List<Project> projects;

    @Override
    public TeamDTO toDTO() {
        return TeamDTO.builder()
                .id(id)
                .name(name)
                .warehouseId(warehouse != null ? warehouse.getId() : null)
                .leaderId(leader != null ? leader.getId() : null)
                .membersIds(members != null && !members.isEmpty() ? members.stream().map(User::getId).toList() : null)
                .build();
    }
}

