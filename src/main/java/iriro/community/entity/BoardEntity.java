package iriro.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "board")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardEntity {

}
