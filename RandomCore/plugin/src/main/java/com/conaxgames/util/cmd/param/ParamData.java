package com.conaxgames.util.cmd.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.conaxgames.util.cmd.ParamType;

@Data
@AllArgsConstructor
public class ParamData {

	private String name;
	private String defaultValue;
	private ParamType type;

}
