# Android-Drawables-Maven-Plugin m2e Configurator

## Getting Started

### Prerequisities

Eclipse 3.7 is recommended, see http://www.eclipse.org/downloads/  
m2e plugin 1.0+, see http://www.eclipse.org/m2e/

### Recommended Configuration ###

As current the ADT plugin doesn't support alternative locations for resource files the recommended method when using the Android-Drawables-Maven-Plugin with Eclipse is to create a profile and use that to unpack the drawables into the ```${baseDir}```.  An example is given below.

    <profiles>  
        <profile>  
            <id>unpack-drawables</id>  
            <build>  
                <plugins>  
                    <plugin>  
                        <groupId>com.github.kingamajick.admp</groupId>  
                        <artifactId>android-drawables-maven-plugin</artifactId>  
                        <version>0.0.1-SNAPSHOT</version>  
                        <extensions>true</extensions>  
                        <executions>  
                            <execution>  
                                <phase>initialize</phase>  
                                <goals>  
                                    <goal>unpack</goal>  
                                </goals>  
                                <configuration>  
                                    <drawableArtifacts>  
                                        <drawableArtifact>  
                                            <groupId>test</groupId>  
                                            <artifactId>android-drawable-plugin-test-source</artifactId>  
                                            <version>0.0.1-SNAPSHOT</version>  
                                        </drawableArtifact>  
                                    </drawableArtifacts>  
                                    <unpackLocation>${basedir}</unpackLocation>  
                                </configuration>  
                            </execution>  
                        </executions>  
                    </plugin>  
                </plugins>  
            </build>  
        </profile>  
    </profiles>

Note: This will not remove the drawable resources that are no longer part of the artifact in subsequent builds.  For this reason, it recommended that you clean down any bitmap files from the drawable directories and do another build if a resource is removed.

I may look at adding a option to perform this on a build.

### Update Sites

#### Snapshot ####

http://android-drawables-maven-plugin-m2e.googlecode.com/svn/trunk/snapshot/