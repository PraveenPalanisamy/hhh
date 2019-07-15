package com.accolite.pru.health.AuthApp.model.payload;

//import com.accolite.pru.health.AuthApp.validation.annotation.NullOrNotBlank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "Registration Request", description = "The registration request payload")
public class RegistrationRequest {

   // @NullOrNotBlank(message = "Registration username can be null but not blank")
    @ApiModelProperty(value = "A valid username", allowableValues = "NonEmpty String")
    private String name;

   // @NullOrNotBlank(message = "Registration email can be null but not blank")
    @ApiModelProperty(value = "A valid email", required = true, allowableValues = "NonEmpty String")
    private String email;

    @NotNull(message = "Registration password cannot be null")
    @ApiModelProperty(value = "A valid password string", required = true, allowableValues = "NonEmpty String")
    private String password;
    
    @NotNull(message = "Registration mobile cannot be null")
    @ApiModelProperty(value = "A valid mobile string", required = true, allowableValues = "NonEmpty String")
    private String mobile;

    @NotNull(message = "Specify whether the user has to be registered as an admin or not")
    @ApiModelProperty(value = "Flag denoting whether the user is an admin or not", required = true,
            dataType = "boolean", allowableValues = "true, false")
    private Boolean registerAsAdmin;
    
    @NotNull(message = "Specify whether the user has to be registered as an patient or not")
    @ApiModelProperty(value = "Flag denoting whether the user is an patient or not", required = true,
            dataType = "boolean", allowableValues = "true, false")
    private Boolean registerAsPatient;
    
    @NotNull(message = "Specify whether the user has to be registered as an doctor or not")
    @ApiModelProperty(value = "Flag denoting whether the user is an doctor or not", required = true,
            dataType = "boolean", allowableValues = "true, false")
    private Boolean registerAsDoctor;

    public RegistrationRequest(String name, String email,
                               String password, String mobile, Boolean registerAsAdmin, Boolean registerAsPatient, Boolean registerAsDoctor) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.registerAsAdmin = registerAsAdmin;
        this.registerAsPatient = registerAsPatient;
        this.registerAsDoctor = registerAsDoctor;
    }

    public RegistrationRequest() {
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Boolean getRegisterAsAdmin() {
        return registerAsAdmin;
    }

    public void setRegisterAsAdmin(Boolean registerAsAdmin) {
        this.registerAsAdmin = registerAsAdmin;
    }

	public Boolean getRegisterAsPatient() {
		return registerAsPatient;
	}

	public void setRegisterAsPatient(Boolean registerAsPatient) {
		this.registerAsPatient = registerAsPatient;
	}

	public Boolean getRegisterAsDoctor() {
		return registerAsDoctor;
	}

	public void setRegisterAsDoctor(Boolean registerAsDoctor) {
		this.registerAsDoctor = registerAsDoctor;
	}
}
