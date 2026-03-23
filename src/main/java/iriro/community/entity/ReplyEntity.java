package iriro.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "reply")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyEntity {
}
