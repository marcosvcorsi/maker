package com.github.gustavovitor.maker.resource;

import com.github.gustavovitor.interfaces.ResourceInterface;
import com.github.gustavovitor.maker.service.MongoServiceMaker;
import com.github.gustavovitor.util.ObjectPageableRequest;
import com.github.gustavovitor.util.RequestPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.ReflectionException;

import static java.util.Objects.nonNull;

@SuppressWarnings({"unchecked"})
public class SecurityMongoResourceMaker<S extends MongoServiceMaker, T, ID, SPO> implements ResourceInterface<S, T, ID, SPO> {

    @Autowired
    private S service;

    protected S getService() {
        return service;
    }

    @Override
    @GetMapping("{objectId}")
    @PreAuthorize("hasAuthority(#root.this.roleRead)")
    public ResponseEntity<T> findById(@PathVariable("objectId") ID objectId) {
        return (ResponseEntity<T>) ResponseEntity.ok(service.findById(objectId));
    }

    @Override
    @PutMapping("/search")
    @PreAuthorize("hasAuthority(#root.this.roleRead)")
    public ResponseEntity<Iterable<T>> findAll(@RequestBody T object) throws ReflectionException {
        return ResponseEntity.ok(service.findAll(object));
    }

    @Override
    @PutMapping("/search/page")
    @PreAuthorize("hasAuthority(#root.this.roleRead)")
    public ResponseEntity<Page<T>> findAllPageable(@RequestBody ObjectPageableRequest<SPO> request) throws ReflectionException {
        return ResponseEntity.ok(service.findAllPageable(request.getObject(), getPageable(request.getPageable())));
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority(#root.this.roleWrite)")
    public ResponseEntity<T> insert(@RequestBody T object) {
        return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.CREATED).body(service.insert(object));
    }

    @Override
    @PutMapping("/{objectId}")
    @PreAuthorize("hasAuthority(#root.this.roleWrite)")
    public ResponseEntity<T> update(@PathVariable ID objectId, @RequestBody T object) {
        return (ResponseEntity<T>) ResponseEntity.ok(service.update(objectId, object));
    }

    @Override
    @PatchMapping("/{objectId}")
    @PreAuthorize("hasAuthority(#root.this.roleWrite)")
    public ResponseEntity<T> patch(@PathVariable ID objectId, @RequestBody T object) {
        return (ResponseEntity<T>) ResponseEntity.ok(service.patch(objectId, object));
    }

    @Override
    @DeleteMapping("/{objectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(#root.this.roleDelete)")
    public void delete(@PathVariable ID objectId) {
        service.delete(objectId);
    }

    protected String getRoleRead() {
        return "";
    }

    protected String getRoleWrite() {
        return "";
    }

    protected String getRoleDelete() {
        return "";
    }

    protected Pageable getPageable(RequestPageable pageable) {
        if (nonNull(pageable.getSort()))
            return PageRequest.of(pageable.getPage(), pageable.getSize(), pageable.getSort());
        else
            return PageRequest.of(pageable.getPage(), pageable.getSize());
    }

}
