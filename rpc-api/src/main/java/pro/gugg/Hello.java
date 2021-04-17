package pro.gugg;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author GZZ
 */
@AllArgsConstructor
@Data
public class Hello implements Serializable {
    String name;
    String message;

}
