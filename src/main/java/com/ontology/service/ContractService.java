package com.ontology.service;


import com.ontology.controller.vo.ContractVo;

public interface ContractService {

    void invokeContract(String action, ContractVo contractVo) throws Exception;
}
