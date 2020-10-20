package cc.w0rm.ghost.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class Rule {
    private Set<String> black;
    private Set<String> white;
}