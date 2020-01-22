package de.mennomax.astikorcarts.oregon;

import java.util.Scanner;

// https://archive.org/download/creativecomputing-1978-05/Creative_Computing_v04_n03_1978_May-June.pdf
// pg. 132-139 (140-147)
public class Oregon extends BasicProgram {
    // TOTAL MILEAGE WHOLE TRIP
    private float m = 0;
    // TURN NUMBER FOR SETTING DATE
    private int d3 = 0;
    // CHOICE OF SHOOTING EXPERTISE LEVEL
    private float d9;
    // AMOUNT SPENT ON ANIMALS
    private float a;
    // AMOUNT SPENT ON FOOD
    private float f;
    // AMOUNT SPENT ON AMMUNITION
    private float b;
    // AMOUNT SPENT ON CLOTHING
    private float c;
    // AMOUNT SPENT ON MISCELLANEOUS SUPPLIES
    private float m1;
    // CASH LEFT OVER AFTER INITIAL PURCHASES
    private float t;
    // TOTAL MILEAGE UP THROUGH PREVIOUS TURN
    private float m2;
    // CHOICE OF EATING
    private int e;
    // FLAG FOR FORT OPTION
    private boolean x1 = false;
    // FLAG FOR INJURY
    private boolean k8 = false;
    // FLAG FOR ILLNESS
    private boolean s4 = false;
    // FLAG FOR CLEARING SOUTH PASS
    private boolean f1 = false;
    // FLAG FOR CLEARING BLUE MOUNTAINS
    private boolean f2 = false;
    // FLAG FOR CLEARING SOUTH PASS IN SETTING MILEAGE
    private boolean m9 = false;

    public Oregon(final IO io) {
        super(io);
    }

    @Override
    public void run() {
        this.print("DO YOU NEED INSTRUCTIONS  (YES/NO)?");
        final String cs = this.inputString();
        if (!"NO".equals(cs)) {
            this.print("THIS PROGRAM SIMULATES A TRIP OVER THE OREGON TRAIL FROM");
            this.print("INDEPENDENCE, MISSOURI TO OREGON CITY, OREGON IN 1847.");
            this.print("YOUR FAMILY OF FIVE WILL COVER THE 2040 MILE OREGON TRAIL");
            this.print("IN 5-6 MONTHS --- IF YOU MAKE IT ALIVE.");
            this.print("");
            this.print("YOU HAD SAVED $900 TO SPEND FOR THE TRIP, AND YOU'VE JUST");
            this.print("   PAID $200 FOR A WAGON.");
            this.print("YOU WILL NEED TO SPEND THE REST OF YOUR MONEY ON THE");
            this.print("   FOLLOWING ITEMS:");
            this.print("");
            this.print("     OXEN - YOU CAN SPEND $200-$300 ON YOUR TEAM");
            this.print("            THE MORE YOU SPEND, THE FASTER YOU'LL GO");
            this.print("               BECAUSE YOU'LL HAVE BETTER ANIMALS");
            this.print("");
            this.print("     FOOD - THE MORE YOU HAVE, THE LESS CHANCE THERE");
            this.print("               IS OF GETTING SICK");
            this.print("");
            this.print("     AMMUNITION - 81 BUYS A BELT OF 50 BULLETS");
            this.print("            YOU WILL NEED BULLETS FOR ATTACKS BY ANIMALS");
            this.print("               AND BANDITS, AND FOR HUNTING FOOD");
            this.print("");
            this.print("     CLOTHING - THIS IS ESPECIALLY IMPORTANT FOR THE COLD");
            this.print("               WEATHER YOU WILL ENCOUNTER WHEN CROSSING");
            this.print("               THE MOUNTAINS");
            this.print("");
            this.print("     MISCELLANEOUS SUPPLIES - THIS INCLUDES MEDICINE AND");
            this.print("               OTHER THINGS YOU WILL NEED FOR SICKNESS");
            this.print("               AND EMERGENCY REPAIRS");
            this.print("");
            this.print("");
            this.print("YOU CAN SPEND ALL YOUR MONEY BEFORE YOU START YOUR TRIP -");
            this.print("OR YOU CAN SAVE SOME OF YOUR CASH TO SPEND AT FORTS ALONG");
            this.print("THE WAY WHEN YOU RUN LOW. HOWEVER, ITEMS COST MORE AT");
            this.print("THE FORTS.  YOU CAN ALSO GO HUNTING ALONG THE WAY TO GET");
            this.print("MORE FOOD.");
            this.print("WHENEVER YOU HAVE TO USE YOUR TRUSTY RIFLE ALONG THE WAY,");
            this.print("YOU WILL BE TOLD TO TYPE IN A WORD (ONE THAT SOUNDS LIKE A");
            this.print("GUN SHOT).  THE FASTER YOU TYPE IN THAT WORD AND HIT THE");
            this.print("\"RETURN\" KEY, THE BETTER LUCK YOU'LL HAVE WITH YOUR GUN.");
            this.print("");
            this.print("AT EACH TURN, ALL ITEMS ARE SHOWN IN DOLLAR AMOUNTS");
            this.print("EXCEPT BULLETS");
            this.print("WHEN ASKED TO ENTER MONEY AMOUNTS, DON'T USE A \"$\".");
            this.print("");
            this.print("GOOD LUCK!!!");
        }
        this.print("");
        this.print("");
        this.print("HOW GOOD A SHOT ARE YOU WITH YOUR RIFLE?");
        this.print("  (1) ACE MARKSMAN,  (2) GOOD SHOT,  (3) FAIR TO MIDDLIN'");
        this.print("         (4) NEED MORE PRACTICE,  (5) SHAKY KNEES");
        this.print("ENTER ONE OF THE ABOVE -- THE BETTER YOU CLAIM YOU ARE, THE");
        this.print("FASTER YOU'LL HAVE TO BE WITH YOUR GUN TO BE SUCCESSFUL.");
        this.d9 = this.inputNumeric();
        if (this.d9 > 5) {
            this.d9 = 0;
        }
        // INITIAL PURCHASES
        while (true) {
            this.print("");
            this.print("");
            this.print("HOW MUCH DO YOU WANT TO SPEND ON YOUR OXEN TEAM?");
            while (true) {
                this.a = this.inputNumeric();
                if (this.a < 200) {
                    this.print("NOT ENOUGH");
                } else if (this.a > 300) {
                    this.print("TOO MUCH");
                } else {
                    break;
                }
            }
            this.print("HOW MUCH DO YOU WANT TO SPEND ON FOOD?");
            while (true) {
                this.f = this.inputNumeric();
                if (this.f < 0) {
                    this.print("IMPOSSIBLE");
                } else {
                    break;
                }
            }
            this.print("HOW MUCH DO YOU WANT TO SPEND ON AMMUNITION?");
            while (true) {
                this.b = this.inputNumeric();
                if (this.b < 0) {
                    this.print("IMPOSSIBLE");
                } else {
                    break;
                }
            }
            this.print("HOW MUCH DO YOU WANT TO SPEND ON CLOTHING?");
            while (true) {
                this.c = this.inputNumeric();
                if (this.c < 0) {
                    this.print("IMPOSSIBLE");
                } else {
                    break;
                }
            }
            this.print("HOW MUCH DO YOU WANT TO SPEND ON MISCELLANEOUS SUPPLIES?");
            while (true) {
                this.m1 = this.inputNumeric();
                if (this.m1 < 0) {
                    this.print("IMPOSSIBLE");
                } else {
                    break;
                }
            }
            this.t = 700 - this.a - this.f - this.b - this.c - this.m1;
            if (this.t < 0) {
                this.print("YOU OVERSPENT--YOU ONLY HAD $700 TO SPEND.  BUY AGAIN");
            } else {
                break;
            }
        }
        this.b = 50 * this.b;
        this.print("AFTER ALL YOUR PURCHASES, YOU NOW HAVE " + this.t + " DOLLARS LEFT");
        this.print("");
        this.print("MONDAY MARCH 29 1847");
        this.print("");
        this.turn();
        while (this.m < 2040) {
            // SETTING DATE
            this.d3 = this.d3 + 1;
            this.print("");
            this.print("MONDAY ");
            switch (this.d3) {
                default:
                case 1:
                    this.print("APRIL 12 ");
                    break;
                case 2:
                    this.print("APRIL 16 ");
                    break;
                case 3:
                    this.print("MAY 10 ");
                    break;
                case 4:
                    this.print("MAY 24 ");
                    break;
                case 5:
                    this.print("JUNE 7 ");
                    break;
                case 6:
                    this.print("JUNE 21 ");
                    break;
                case 7:
                    this.print("JULY 5 ");
                    break;
                case 8:
                    this.print("JULY 19 ");
                    break;
                case 9:
                    this.print("AUGUST 2 ");
                    break;
                case 10:
                    this.print("AUGUST 16 ");
                    break;
                case 11:
                    this.print("AUGUST 31 ");
                    break;
                case 12:
                    this.print("SEPTEMBER 13 ");
                    break;
                case 13:
                    this.print("SEPTEMBER 27 ");
                    break;
                case 14:
                    this.print("OCTOBER 11 ");
                    break;
                case 15:
                    this.print("OCTOBER 25 ");
                    break;
                case 16:
                    this.print("NOVEMBER 8 ");
                    break;
                case 17:
                    this.print("NOVEMBER 22 ");
                    break;
                case 18:
                    this.print("DECEMBER 6 ");
                    break;
                case 19:
                    this.print("DECEMBER 20 ");
                    break;
                case 20:
                    this.print("YOU HAVE BEEN ON THE TRAIL TOO LONG ------");
                    this.print("YOUR FAMILY DIES IN THE FIRST BLIZZARD OF WINTER");
                    this.formalities();
                    return;
            }
            this.print("1847");
            this.print("");
            if (this.turn()) {
                return;
            }
        }
        this.finalTurn();
    }

    // BEGINNING EACH TURN
    private boolean turn() {
        if (this.f < 0) {
            this.f = 0;
        }
        if (this.b < 0) {
            this.b = 0;
        }
        if (this.c < 0) {
            this.c = 0;
        }
        if (this.m1 < 0) {
            this.m1 = 0;
        }
        if (this.f < 13) {
            this.print("YOU'D BETTER DO SOME HUNTING OR BUY FOOD AND SOON!!!!");
        }
        this.f = this.round(this.f);
        this.b = this.round(this.b);
        this.c = this.round(this.c);
        this.m1 = this.round(this.m1);
        this.t = this.round(this.t);
        this.m = this.round(this.m);
        this.m2 = this.m;
        if (this.s4 || this.k8) {
            this.t = this.t - 20;
            if (this.t < 0) {
                this.noDoctor();
                return true;
            }
            this.print("DOCTOR'S BILL IS $20");
            this.k8 = this.s4 = false;
        }
        if (this.m9) {
            this.print("TOTAL MILEAGE IS 950");
        } else {
            this.print("TOTAL MILEAGE IS " + this.m);
        }
        this.m9 = false;
        this.print("FOOD", "BULLETS", "CLOTHING", "MISC. SUPP.", "CASH");
        this.print(this.f, this.b, this.c, this.m1, this.t);
        this.wants();
        if (this.f < 13) {
            this.noFood();
            return true;
        }
        this.eat();
        this.m = this.m + 200 + (this.a - 220) / 5.0F + 10 * this.rnd();
        if (this.ridersAttack()) {
            return true;
        }
        if (this.events()) {
            return true;
        }
        return this.mountains();
    }

    private void wants() {
        this.x1 = !this.x1;
        while (true) {
            float x;
            if (this.x1) {
                this.print("DO YOU WANT TO (1) HUNT, OR (2) CONTINUE?");
                x = this.inputNumeric();
                if (x != 1) {
                    x = 2;
                }
                x = x + 1;
            } else {
                this.print("DO YOU WANT TO (1) STOP AT THE NEXT FORT, (2) HUNT, ");
                this.print("OR (3) CONTINUE?");
                x = this.inputNumeric();
                if (x > 2 || x < 1) {
                    x = 3;
                }
            }
            switch (this.round(x)) {
                default:
                case 1:
                    this.stopAtFort();
                    break;
                case 2:
                    if (this.hunting()) {
                        continue;
                    }
                    break;
                case 3:
                    break;
            }
            break;
        }
    }

    // STOPPING AT FORT
    private void stopAtFort() {
        this.print("ENTER WHAT YOU WISH TO SPEND ON THE FOLLOWING");
        this.print("FOOD?");
        float p = this.spend();
        this.f = this.f + 2.0F / 3.0F * p;
        this.print("AMMUNITION?");
        p = this.spend();
        this.b = this.round(this.b + 2.0F / 3.0F * p * 50);
        this.print("CLOTHING?");
        p = this.spend();
        this.c = this.c + 2.0F / 3.0F * p;
        this.print("MISCELLANEOUS SUPPLIES?");
        p = this.spend();
        this.m1 = this.m1 + 2.0F / 3.0F * p;
        this.m = this.m - 45;
    }

    private float spend() {
        final float p = this.inputNumeric();
        if (p < 0) {
            return p;
        }
        this.t = this.t - p;
        if (this.t < 0) {
            this.print("YOU DON'T HAVE THAT MUCH--KEEP YOUR SPENDING DOWN");
            this.print("YOU MISS YOUR CHANCE TO SPEND ON THAT ITEM");
            this.t = this.t + p;
            return 0.0F;
        }
        return p;
    }

    // HUNTING
    private boolean hunting() {
        if (this.b <= 39) {
            this.print("TOUGH---YOU NEED MORE BULLETS TO GO HUNTING");
            return true;
        } else {
            this.m = this.m - 45;
            final int b1 = this.shoot();
            if (b1 <= 1) {
                // BELLS IN LINE
                this.print("RIGHT BETWEEN THE EYES---YOU GOT A BIG ONE!!!!");
                this.print("FULL BELLIES TONIGHT!");
                this.f = this.f + 52 + this.rnd() * 6;
                this.b = this.b - 10 - this.rnd() * 4;
            } else if (100 * this.rnd() < 13 * b1) {
                this.print("YOU MISSED---AND YOUR DINNER GOT AWAY.....");
            } else {
                this.print("NICE SHOT--RIGHT ON TARGET--GOOD EATIN' TONIGHT!!");
                this.f = this.f + 48 - 2 * b1;
                this.b = this.b - 10 - 3 * b1;
            }
        }
        return false;
    }

    // EATING
    private void eat() {
        while (true) {
            this.print("DO YOU WANT TO EAT (1) POORLY, (2) MODERATELY, ");
            this.print("OR (3) WELL?");
            this.e = this.round(this.inputNumeric());
            if (this.e < 1 || this.e > 3) {
                continue;
            }
            this.f = this.f - 8 - 5 * this.e;
            if (this.f < 0) {
                this.f = this.f + 8 + 5 * this.e;
                this.print("YOU CAN'T EAT THAT WELL");
                continue;
            }
            break;
        }
    }

    // RIDERS ATTACK
    private boolean ridersAttack() {
        if (this.rnd() * 10 <= (this.pow(this.m / 100.0F - 4, 2) + 72) / (this.pow(this.m / 100.0F - 4, 2) + 12) - 1) {
            this.print("RIDERS AHEAD.  THEY ");
            boolean s5 = false;
            if (this.rnd() >= 0.8F) {
                this.print("DON'T ");
                s5 = true;
            }
            this.print("LOOK HOSTILE");
            this.print("TACTICS");
            float t1;
            while (true) {
                this.print("(1) RUN  (2) ATTACK  (3) CONTINUE  (4) CIRCLE WAGONS");
                if (this.rnd() <= 0.2F) {
                    s5 = !s5;
                }
                t1 = this.inputNumeric();
                if (t1 < 1 || t1 > 4) {
                    continue;
                }
                break;
            }
            t1 = this.round(t1);
            if (s5) {
                if (t1 <= 1) {
                    this.m = this.m + 15;
                    this.a = this.a - 10;
                } else if (t1 <= 2) {
                    this.m = this.m - 5;
                    this.b = this.b - 100;
                } else if (t1 > 3) {
                    this.m = this.m - 20;
                }
                this.print("RIDERS WERE FRIENDLY, BUT CHECK FOR POSSIBLE LOSSES");
            } else {
                if (t1 == 3 && this.rnd() > 0.8F) {
                    this.print("THEY DID NOT ATTACK");
                } else {
                    if (t1 <= 1) {
                        this.m = this.m + 20;
                        this.m1 = this.m1 - 15;
                        this.b = this.b - 150;
                        this.a = this.a - 40;
                    } else if (t1 <= 2) {
                        final int b1 = this.shoot();
                        this.b = this.b - b1 * 40 - 80;
                        this.attackShot(b1);
                    } else if (t1 <= 3) {
                        this.b = this.b - 150;
                        this.m1 = this.m1 - 15;
                    } else {
                        final int b1 = this.shoot();
                        this.b = this.b - b1 * 30 - 80;
                        this.m = this.m - 25;
                        this.attackShot(b1);
                    }
                    this.print("RIDERS WERE HOSTILE--CHECK FOR LOSSES");
                    if (this.b < 0) {
                        this.print("YOU RAN OUT OF BULLETS AND GOT MASSACRED BY THE RIDERS");
                        this.formalities();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void attackShot(final int b1) {
        if (b1 <= 1) {
            this.print("NICE SHOOTING---YOU DROVE THEM OFF");
        } else if (b1 > 4) {
            this.print("LOUSY SHOT---YOU GOT KNIFED");
            this.k8 = true;
            this.print("YOU HAVE TO SEE OL' DOC BLANCHARD");
        } else {
            this.print("KINDA SLOW WITH YOUR COLT .45");
        }
    }

    // SELECTION OF EVENTS
    private boolean events() {
        int d1;
        final int[] d = { 6, 11, 13, 15, 17, 22, 32, 35, 37, 42, 44, 54, 64, 69, 95 };
        final float r1 = 100 * this.rnd();
        for (d1 = 0 ; d1 < 15 && r1 > d[d1]; d1++);
        switch (d1) {
            case 0:
                this.print("WAGON BREAKS DOWN--LOSE TIME AND SUPPLIES FIXING IT");
                this.m = this.m - 15 - 5 * this.rnd();
                this.m1 = this.m1 - 8;
                break;
            case 1:
                this.print("OX INJURES LEG---SLOWS YOU DOWN REST OF TRIP");
                this.m = this.m - 25;
                this.a = this.a - 20;
                break;
            case 2:
                this.print("BAD LUCK---YOUR DAUGHTER BROKE HER ARM");
                this.print("YOU HAD TO STOP AND USE SUPPLIES TO MAKE A SLING");
                this.m = this.m - 5 - 4 * this.rnd();
                this.m1 = this.m1 - 2 - 3 * this.rnd();
                break;
            case 3:
                this.print("OX WANDERS OFF---SPEND TIME LOOKING FOR IT");
                this.m = this.m - 17;
                break;
            case 4:
                this.print("YOUR SON GETS LOST---SPEND HALF THE DAY LOOKING FOR HIM");
                this.m = this.m - 10;
                break;
            case 5:
                this.print("UNSAFE WATER--LOSE TIME LOOKING FOR CLEAN SPRING");
                this.m = this.m - 10 * this.rnd() - 2;
                break;
            case 6:
                if (this.m <= 950) {
                    this.print("HEAVY RAINS---TIME AND SUPPLIES LOST");
                    this.f = this.f - 10;
                    this.b = this.b - 500;
                    this.m1 = this.m1 - 15;
                    this.m = this.m - 10 * this.rnd() - 5;
                } else {
                    this.print("COLD WEATHER---BRRRRRRR!---YOU ");
                    boolean c1 = false;
                    if (this.c <= 22 + 4 * this.rnd()) {
                        this.print("DON'T ");
                        c1 = true;
                    }
                    this.print("HAVE ENOUGH CLOTHING TO KEEP YOU WARM");
                    if (c1) {
                        if (this.illness()) {
                            return true;
                        }
                    }
                }
                break;
            case 7:
                this.print("BANDITS ATTACK");
                int b1 = this.shoot();
                this.b = this.b - 20 * b1;
                if (this.b < 0) {
                    this.print("YOU RAN OUT OF BULLETS---THEY GET LOTS OF CASH");
                    this.t = this.t / 3.0F;
                } else {
                    if (b1 > 1) {
                        this.print("YOU GOT SHOT IN THE LEG AND THEY TOOK ONE OF YOUR OXEN");
                        this.k8 = true;
                        this.print("BETTER HAVE A DOC LOOK AT YOUR WOUND");
                        this.m1 = this.m1 - 5;
                        this.a = this.a - 20;
                    } else {
                        this.print("QUICKEST DRAW OUTSIDE OF DODGE CITY!!!");
                        this.print("YOU GOT 'EM!");
                    }
                }
                break;
            case 8:
                this.print("THERE WAS A FIRE IN YOUR WAGON--FOOD AND SUPPLIES DAMAGE!");
                this.f = this.f - 40;
                this.b = this.b - 400;
                this.m1 = this.m1 - this.rnd() * 8 - 3;
                this.m = this.m - 15;
                break;
            case 9:
                this.print("LOSE YOUR WAY IN HEAVY FOG---TIME IS LOST");
                this.m = this.m - 10 - 5 * this.rnd();
                break;
            case 10:
                this.print("YOU KILLED A POISONOUS SNAKE AFTER IT BIT YOU");
                this.b = this.b - 10;
                this.m1 = this.m1 - 5;
                if (this.m1 < 0) {
                    this.print("YOU DIE OF SNAKEBITE SINCE YOU HAVE NO MEDICINE");
                    this.formalities();
                    return true;
                }
                break;
            case 11:
                this.print("WAGON GETS SWAMPED FORDING RIVER--LOSE FOOD AND CLOTHES");
                this.f = this.f - 30;
                this.c = this.c - 20;
                this.m = this.m - 20 - 20 * this.rnd();
                break;
            case 12:
                this.print("WILD ANIMALS ATTACK!");
                b1 = this.shoot();
                if (this.b <= 39) {
                    this.print("YOU WERE TOO LOW ON BULLETS--");
                    this.print("THE WOLVES OVERPOWERED YOU");
                    this.k8 = true;
                    this.die();
                    return true;
                }
                if (b1 <= 2) {
                    this.print("NICE SHOOTIN' PARDNER---THEY DIDN'T GET MUCH");
                } else {
                    this.print("SLOW ON THE DRAW---THEY GOT AT YOUR FOOD AND CLOTHES");
                }
                this.b = this.b - 20 * b1;
                this.c = this.c - b1 * 4;
                this.f = this.f - b1 * 8;
                break;
            case 13:
                this.print("HAIL STORM---SUPPLIES DAMAGED");
                this.m = this.m - 5 - this.rnd() * 10;
                this.b = this.b - 200;
                this.m1 = this.m1 - 4 - this.rnd() * 3;
                break;
            case 14:
                if (this.e == 1) {
                    if (this.illness()) {
                        return true;
                    }
                } else if (this.e == 3) {
                    if (this.rnd() < 0.5F) {
                        if (this.illness()) {
                            return true;
                        }
                    }
                } else {
                    if (this.rnd() > 0.25F) {
                        if (this.illness()) {
                            return true;
                        }
                    }
                }
                break;
            default:
                this.print("HELPFUL INDIANS SHOW YOU WERE TO FIND MORE FOOD");
                this.f = this.f + 14;
        }
        return false;
    }

    // MOUNTAINS
    private boolean mountains() {
        if (this.m <= 950) {
            return false;
        }
        if (this.rnd() * 10 <= 9 - (this.pow(this.m / 100.0F - 15, 2) + 72) / (this.pow(this.m / 100.0F - 15, 2) + 12)) {
            this.print("RUGGED MOUNTAINS");
            if (this.rnd() <= 0.1F) {
                this.print("YOU GOT LOST---LOSE VALUABLE TIME TRYING TO FIND TRAIL!");
                this.m = this.m - 60;
            } else {
                if (this.rnd() <= 0.11F) {
                    this.print("WAGON DAMAGED!---LOSE TIME AND SUPPLIES");
                    this.m1 = this.m1 - 5;
                    this.b = this.b - 200;
                    this.m = this.m - 20 - 30 * this.rnd();
                } else {
                    this.print("THE GOING GETS SLOW");
                    this.m = this.m - 45 - this.rnd() / 0.02F;
                }
            }
        }
        if (this.mountainPass()) {
            return true;
        }
        if (this.m <= 950) {
            this.m9 = true;
        }
        return false;
    }

    private boolean mountainPass() {
        if (!this.f1) {
            this.f1 = true;
            if (this.rnd() < 0.8F) {
                return this.blizzard();
            } else {
                this.print("YOU MADE IT SAFELY THROUGH SOUTH PASS--NO SNOW");
            }
        }
        if (this.m >= 1700) {
            if (!this.f2) {
                this.f2 = true;
                if (this.rnd() < 0.7F) {
                    return this.blizzard();
                }
            }
        }
        return false;
    }

    private boolean blizzard() {
        this.print("BLIZZARD IN MOUNTAIN PASS--TIME AND SUPPLIES LOST");
        this.f = this.f - 25;
        this.m1 = this.m1 - 10;
        this.b = this.b - 300;
        this.m = this.m - 30 - 40 * this.rnd();
        if (this.c < 18 + 2 * this.rnd()) {
            return this.illness();
        }
        return false;
    }

    // DYING
    private void noFood() {
        this.print("YOU RAN OUT OF FOOD AND STARVED TO DEATH");
        this.formalities();
    }

    private void noDoctor() {
        this.t = 0;
        this.print("YOU CAN'T AFFORD A DOCTOR");
        this.die();
    }

    private void noMedicalSupplies() {
        this.print("YOU RAN OUT OF MEDICAL SUPPLIES");
        this.die();
    }

    private void die() {
        this.print("YOU DIED OF ");
        if (this.k8) {
            this.print("INJURIES");
        } else {
            this.print("PNEUMONIA");
        }
        this.formalities();
    }

    private void finalTurn() {
        // FINAL TURN
        float f9 = (2040 - this.m2) / (this.m - this.m2);
        this.f = this.f + (1 - f9) * (8 * 5 * this.e);
        this.print("");

        // BELLS IN LINES
        this.print("YOU FINALLY ARRIVED AT OREGON CITY");
        this.print("AFTER 2040 LONG MILES---HOORAY!!!!!");
        this.print("A REAL PIONEER!");
        this.print("");
        f9 = this.round(f9 * 14);
        this.d3 = this.d3 * 14 + (int) f9;
        f9 = f9 + 1;
        if (f9 >= 8) {
            f9 = f9 - 7;
        }
        switch ((int) f9) {
            default:
            case 1:
                this.print("MONDAY ");
                break;
            case 2:
                this.print("TUESDAY ");
                break;
            case 3:
                this.print("WEDNESDAY ");
                break;
            case 4:
                this.print("THURSDAY ");
                break;
            case 5:
                this.print("FRIDAY ");
                break;
            case 6:
                this.print("SATURDAY ");
                break;
            case 7:
                this.print("SUNDAY ");
                break;
        }
        if (this.d3 <= 124) {
            this.d3 = this.d3 - 93;
            this.print("JULY " + this.d3 + " 1847");
        } else if (this.d3 <= 155) {
            this.d3 = this.d3 - 124;
            this.print("AUGUST " + this.d3 + " 1847");
        } else if (this.d3 <= 185) {
            this.d3 = this.d3 - 155;
            this.print("SEPTEMBER " + this.d3 + " 1847");
        } else if (this.d3 <= 216) {
            this.d3 = this.d3 - 185;
            this.print("OCTOBER " + this.d3 + " 1847");
        } else if (this.d3 <= 246) {
            this.d3 = this.d3 - 216;
            this.print("NOVEMBER " + this.d3 + " 1847");
        } else {
            this.d3 = this.d3 - 246;
            this.print("DECEMBER " + this.d3 + " 1847");
        }
        this.print("");
        this.print("FOOD", "BULLETS", "CLOTHING", "MISC. SUPP.", "CASH");
        if (this.b < 0) {
            this.b = 0;
        }
        if (this.c < 0) {
            this.c = 0;
        }
        if (this.m1 < 0) {
            this.m1 = 0;
        }
        if (this.t < 0) {
            this.t = 0;
        }
        if (this.f < 0) {
            this.f = 0;
        }
        this.print(this.round(this.f), this.round(this.b), this.round(this.c), this.round(this.m1), this.round(this.t));
        this.print("");
        this.print("           PRESIDENT JAMES K. POLK SENDS YOU HIS");
        this.print("                 HEARTIEST CONGRATULATIONS");
        this.print("");
        this.print("           AND WISHES YOU A PROSPEROUS LIFE AHEAD");
        this.print("");
        this.print("                      AT YOUR NEW HOME");
    }

    private void formalities() {
        this.print("");
        this.print("DUE TO YOUR UNFORTUNATE SITUATION, THERE ARE A FEW");
        this.print("FORMALITIES WE MUST GO THROUGH");
        this.print("");
        this.print("WOULD YOU LIKE A MINISTER?");
        this.inputString();
        this.print("WOULD YOU LIKE A FANCY FUNERAL?");
        this.inputString();
        this.print("WOULD YOU LIKE US TO INFORM YOUR NEXT OF KIN?");
        final String cs = this.inputString();
        if ("YES".equals(cs)) {
            this.print("THAT WILL BE $4.50 FOR THE TELEGRAPH CHARGE.");
        } else {
            this.print("BUT YOUR AUNT SADIE IN ST. LOUIS IS REALLY WORRIED ABOUT YOU");
        }
        this.print("");
        this.print("WE THANK YOU FOR THIS INFORMATION AND WE ARE SORRY YOU");
        this.print("DIDN'T MAKE IT TO THE GREAT TERRITORY OF OREGON");
        this.print("BETTER LUCK NEXT TIME");
        this.print("");
        this.print("");
        this.print("                              SINCERELY");
        this.print("");
        this.print("                 THE OREGON CITY CHAMBER OF COMMERCE");
    }

    // SHOOTING SUB-ROUTINE
    private int shoot() {
        final String[] ss = { "BANG", "BLAM", "POW", "WHAM" };
        final int s6 = this.round(this.rnd() * 4);
        this.print("TYPE " + ss[s6]);
        final long b3 = this.clk();
        final String cs = this.inputString();
        final long b1 = this.clk();
        // TODO: tune this to be playable
        int b = (int) (((b1 - b3) * 3600) - (this.d9 - 1));
        this.print("");
        if (b <= 0) {
            b = 0;
        }
        if (!cs.equals(ss[s6])) {
            b = 9;
        }
        return b;
    }

    // ILLNESS SUB-ROUTINE
    private boolean illness() {
        if (100 * this.rnd() < 10 + 35 * (this.e - 1)) {
            this.print("MILD ILLNESS---MEDICINE USED");
            this.m = this.m - 5;
            this.m1 = this.m1 - 2;
        } else if (100 * this.rnd() < 100 - (40.0F / this.pow(4.0F, this.e - 1))) {
            this.print("BAD ILLNESS---MEDICINE USED");
            this.m = this.m - 5;
            this.m1 = this.m1 - 5;
        } else {
            this.print("SERIOUS ILLNESS");
            this.print("YOU MUST STOP FOR MEDICAL ATTENTION");
            this.m1 = this.m1 - 10;
            this.s4 = true;
        }
        if (this.m1 < 0) {
            this.noMedicalSupplies();
            return true;
        }
        return false;
    }

    public static void main(final String[] args) {
        final Oregon oregon = new Oregon(new IO() {
            final Scanner kb = new Scanner(System.in);

            @Override
            public String input() {
                return this.kb.nextLine();
            }

            @Override
            public void print(final String s) {
                System.out.println(s);
            }
        });
        oregon.run();
    }
}
