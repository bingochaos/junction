package cn.com.navia.sdk.bean;

import java.util.List;

/**
 * Created by gaojie on 15-2-13.
 */
public class META {

    private Update update;

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public class Update {
        private  Building building ;

        public Building getBuilding() {
            return building;
        }

        public void setBuilding(Building building) {
            this.building = building;
        }

        public class Building {
            private String name;
            private int id;

            private List<Floor> floors;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<Floor> getFloors() {
                return floors;
            }

            public void setFloors(List<Floor> floor) {
                this.floors = floors;
            }


            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public class Floor {
                private int gId;
                private int mapId;
                private String name;

                //坐标说明
                private double oLat;
                private double oLng;
                private double angle;
                private double LatdegM;
                private double LngdegM;

                public int getgId() {
                    return gId;
                }

                public void setgId(int gId) {
                    this.gId = gId;
                }

                public int getMapId() {
                    return mapId;
                }

                public void setMapId(int mapId) {
                    this.mapId = mapId;
                }

                //spectrum file
                private String spec_file;
                //location file
                private String loc_file;


                public double getoLat() {
                    return oLat;
                }

                public void setoLat(double oLat) {
                    this.oLat = oLat;
                }

                public double getoLng() {
                    return oLng;
                }

                public void setoLng(double oLng) {
                    this.oLng = oLng;
                }

                public double getAngle() {
                    return angle;
                }

                public void setAngle(double angle) {
                    this.angle = angle;
                }

                public double getLatdegM() {
                    return LatdegM;
                }

                public void setLatdegM(double LatdegM) {
                    this.LatdegM = LatdegM;
                }

                public double getLngdegM() {
                    return LngdegM;
                }

                public void setLngdegM(double LngdegM) {
                    this.LngdegM = LngdegM;
                }

                public String getSpec_file() {
                    return spec_file;
                }

                public void setSpec_file(String spec_file) {
                    this.spec_file = spec_file;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getLoc_file() {
                    return loc_file;
                }

                public void setLoc_file(String loc_file) {
                    this.loc_file = loc_file;
                }
            }
        }
    }
}
