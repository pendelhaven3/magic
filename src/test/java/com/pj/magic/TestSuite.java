package com.pj.magic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.pj.magic.dao.CustomerDaoTest;

@RunWith(Suite.class)
@SuiteClasses({
	CustomerDaoTest.class
})
public class TestSuite {

}
