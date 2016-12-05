package fr.coppernic.utils.grepo.core

import org.junit.Test

/**
 * Created on 05/12/16
 * @author bastien
 */
class ProjectPathLevelComparatorTest {

    @Test
    public void compare() {
        Comparator<String> c = new Workspace.ProjectPathLevelComparator()

        assert 0 == c.compare("beautiful", "beautiful")
        assert 0 == c.compare("az/beauty", "az/beauty")
        assert 0 == c.compare("az/er/ty", "az/er/ty")
        assert 0 < c.compare("string", "appearance")
        assert 0 > c.compare("appearance", "string")
        assert 0 < c.compare("z/a", "z")
        assert 0 > c.compare("z/a", "z/a/b")
        assert 0 < c.compare("z/b", "z/a")
        assert 0 < c.compare("z/a/b", "z/b")
    }
}
