(ns dw-todo-exercise.dw-todo-exercise.http-test
  (:require [clojure.test :refer :all]
            [dw-todo-exercise.http :refer :all]
            [dw-todo-exercise.util :as util])
  (:refer-clojure :exclude [get update]))

(deftest get-test
  (testing "returns 404 error when no todo exists for id"
    (is (= {:status 404
            :headers {}
            :body {:error :not-found}}
           (get :foo {:db (atom {})}))))
  (testing "returns success response w/ todo when it exists"
    (let [todo {:title "Test TODO" :complete false}
          id (util/title->id (:title todo))
          db (atom {})]
      (create {:body todo :db db})
      (is (= {:status 200
              :headers {}
              :body (assoc todo :id id)}
             (clojure.core/update (get id {:db db})
                                  :body dissoc :created-at))))))

(deftest get-all-test
  (testing "returns empty success response w/ empty db"
    (is (= {:status 200
            :headers {}
            :body '()}
           (get-all {:db (atom {})}))))
  (testing "returns all todos in the db"
    (let [todos '({:id :todo-one, :title "TODO One", :complete false}
                  {:id :todo-two, :title "TODO Two", :complete true}
                  {:id :todo-three, :title "TODO Three", :complete false})
          db (atom {})]
      (doseq [todo todos]
        (create {:db db, :body (dissoc todo :id)}))
      (is (= {:status 200
              :headers {}
              :body todos}
             (clojure.core/update (get-all {:db db})
                                  :body
                                  (fn [ts]
                                    (map #(dissoc % :created-at) ts))))))))

(deftest create-test
  (testing "returns success response when it adds the new TODO to the db"
    (let [todo {:title "New TODO", :complete false}
          db (atom {})
          id (util/title->id (:title todo))]
      (is (= {:status 201
              :headers {"Location" (str "/api/todos/" (name id))}
              :body (assoc todo :id id)}
             (clojure.core/update (create {:body todo, :db db})
                                  :body dissoc :created-at)))
      (is (= todo
             (dissoc (:body (get id {:db db})) :id :created-at)))))
  (testing "returns a 500 error if creating throws exception"
    (is (= {:status 500
            :headers {}
            :body {:error "java.lang.NullPointerException"}}
           (create {:body {} :db nil})))))

(deftest update-test
  (testing "returns success response when it updates the TODO in the db"
    (let [todo {:title "Do a thing", :complete false}
          db (atom {})
          created (:body (create {:db db, :body todo}))]
      (is (= {:status 200
              :headers {}
              :body (assoc created :complete true)}
             (update (:id created) {:db db
                                    :body {:complete true}})))
      (is (:complete (:body (get (:id created) {:db db}))))))
  (testing "returns 404 error when TODO id isn't found in db"
    (is (= {:status 404
            :headers {}
            :body {:error :not-found}}
           (update :foo {:db (atom {})
                         :body {:complete true}}))))
  (testing "returns 500 error when updating throws exception"
    (is (= {:status 500
            :headers {}
            :body {:error "java.lang.NullPointerException"}}
           (update :foo {:db nil})))))

(deftest delete-test
  (testing "returns success response when TODO is deleted"
    (let [db (atom {:foo {:title "Foo", :complete true}})]
      (is (= {:status 200
              :headers {}
              :body {:deleted :foo}}
             (delete :foo {:db db})))
      (is (empty? @db))))
  (testing "returns 404 error when TODO id isn't found in db")
  (testing "returns 500 error when deleting throws exception"))