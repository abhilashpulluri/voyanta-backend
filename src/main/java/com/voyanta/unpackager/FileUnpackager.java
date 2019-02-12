package com.voyanta.unpackager;


import com.voyanta.dto.ValidFileDTO;

public interface FileUnpackager {

	ValidFileDTO splitFile() throws Exception;

}
